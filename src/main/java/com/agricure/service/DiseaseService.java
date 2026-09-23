package com.agricure.service;

import com.agricure.dto.disease.DiseaseRequest;
import com.agricure.dto.disease.DiseaseResponse;
import com.agricure.entity.Disease;
import com.agricure.entity.PlantDisease;
import com.agricure.exception.DuplicateResourceException;
import com.agricure.exception.ResourceNotFoundException;
import com.agricure.repository.DiseaseRepository;
import com.agricure.repository.PlantDiseaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DiseaseService {

    private final DiseaseRepository diseaseRepository;
    private final PlantDiseaseRepository plantDiseaseRepository;

    public DiseaseService(DiseaseRepository diseaseRepository, PlantDiseaseRepository plantDiseaseRepository) {
        this.diseaseRepository = diseaseRepository;
        this.plantDiseaseRepository = plantDiseaseRepository;
    }

    @Transactional(readOnly = true)
    public List<DiseaseResponse> findAll() {
        return diseaseRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<DiseaseResponse> search(String query) {
        return diseaseRepository.findByNameContainingIgnoreCase(query).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DiseaseResponse findById(Long id) {
        return toResponse(getDisease(id));
    }

    @Transactional
    public DiseaseResponse create(DiseaseRequest request) {
        if (diseaseRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A disease with this name already exists");
        }
        return toResponse(diseaseRepository.save(apply(new Disease(), request)));
    }

    @Transactional
    public DiseaseResponse update(Long id, DiseaseRequest request) {
        Disease disease = getDisease(id);
        diseaseRepository.findByNameIgnoreCase(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("A disease with this name already exists");
                });
        return toResponse(diseaseRepository.save(apply(disease, request)));
    }

    @Transactional
    public void delete(Long id) {
        Disease disease = getDisease(id);
        List<PlantDisease> mappings = plantDiseaseRepository.findByDiseaseId(id);
        plantDiseaseRepository.deleteAll(mappings);
        diseaseRepository.delete(disease);
    }

    public Disease getDisease(Long id) {
        return diseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disease not found: " + id));
    }

    private Disease apply(Disease disease, DiseaseRequest request) {
        disease.setName(request.name().trim());
        disease.setSeverity(request.severity() == null || request.severity().isBlank() ? "MEDIUM" : request.severity().trim().toUpperCase());
        disease.setSymptoms(request.symptoms().trim());
        disease.setCauses(request.causes().trim());
        disease.setPrevention(request.prevention().trim());
        disease.setTreatment(request.treatment().trim());
        disease.setSymptomKeywords(trimToNull(request.symptomKeywords()));
        return disease;
    }

    private DiseaseResponse toResponse(Disease disease) {
        List<DiseaseResponse.AffectedPlant> plants = plantDiseaseRepository.findWithPlantByDiseaseId(disease.getId())
                .stream()
                .map(mapping -> new DiseaseResponse.AffectedPlant(mapping.getPlant().getId(), mapping.getPlant().getName()))
                .toList();
        return DiseaseResponse.from(disease, plants);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
