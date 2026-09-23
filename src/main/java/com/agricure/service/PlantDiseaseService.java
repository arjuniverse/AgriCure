package com.agricure.service;

import com.agricure.dto.mapping.PlantDiseaseRequest;
import com.agricure.dto.mapping.PlantDiseaseResponse;
import com.agricure.entity.Disease;
import com.agricure.entity.Plant;
import com.agricure.entity.PlantDisease;
import com.agricure.exception.DuplicateResourceException;
import com.agricure.exception.ResourceNotFoundException;
import com.agricure.repository.PlantDiseaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlantDiseaseService {

    private final PlantDiseaseRepository plantDiseaseRepository;
    private final PlantService plantService;
    private final DiseaseService diseaseService;

    public PlantDiseaseService(PlantDiseaseRepository plantDiseaseRepository, PlantService plantService, DiseaseService diseaseService) {
        this.plantDiseaseRepository = plantDiseaseRepository;
        this.plantService = plantService;
        this.diseaseService = diseaseService;
    }

    @Transactional(readOnly = true)
    public List<PlantDiseaseResponse> findAll() {
        return plantDiseaseRepository.findAllWithRelations().stream().map(PlantDiseaseResponse::from).toList();
    }

    @Transactional
    public PlantDiseaseResponse create(PlantDiseaseRequest request) {
        if (plantDiseaseRepository.existsByPlantIdAndDiseaseId(request.plantId(), request.diseaseId())) {
            throw new DuplicateResourceException("This plant is already linked to the selected disease");
        }
        Plant plant = plantService.getPlant(request.plantId());
        Disease disease = diseaseService.getDisease(request.diseaseId());
        PlantDisease mapping = new PlantDisease();
        mapping.setPlant(plant);
        mapping.setDisease(disease);
        mapping.setNotes(request.notes());
        return toResponse(plantDiseaseRepository.save(mapping));
    }

    @Transactional
    public PlantDiseaseResponse update(Long id, PlantDiseaseRequest request) {
        PlantDisease mapping = plantDiseaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant-disease mapping not found: " + id));
        plantDiseaseRepository.findByPlantIdAndDiseaseId(request.plantId(), request.diseaseId())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("This plant is already linked to the selected disease");
                });
        mapping.setPlant(plantService.getPlant(request.plantId()));
        mapping.setDisease(diseaseService.getDisease(request.diseaseId()));
        mapping.setNotes(request.notes());
        return toResponse(plantDiseaseRepository.save(mapping));
    }

    @Transactional
    public void delete(Long id) {
        if (!plantDiseaseRepository.existsById(id)) {
            throw new ResourceNotFoundException("Plant-disease mapping not found: " + id);
        }
        plantDiseaseRepository.deleteById(id);
    }

    private PlantDiseaseResponse toResponse(PlantDisease mapping) {
        mapping.getPlant().getName();
        mapping.getDisease().getName();
        return PlantDiseaseResponse.from(mapping);
    }
}
