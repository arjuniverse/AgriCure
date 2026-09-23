package com.agricure.service;

import com.agricure.dto.plant.PlantRequest;
import com.agricure.dto.plant.PlantResponse;
import com.agricure.entity.Plant;
import com.agricure.entity.PlantDisease;
import com.agricure.exception.DuplicateResourceException;
import com.agricure.exception.ResourceNotFoundException;
import com.agricure.repository.PlantDiseaseRepository;
import com.agricure.repository.PlantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PlantService {

    private final PlantRepository plantRepository;
    private final PlantDiseaseRepository plantDiseaseRepository;

    public PlantService(PlantRepository plantRepository, PlantDiseaseRepository plantDiseaseRepository) {
        this.plantRepository = plantRepository;
        this.plantDiseaseRepository = plantDiseaseRepository;
    }

    @Transactional(readOnly = true)
    public List<PlantResponse> findAll() {
        return plantRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PlantResponse> search(String query) {
        return plantRepository
                .findByNameContainingIgnoreCaseOrScientificNameContainingIgnoreCase(query, query)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlantResponse findById(Long id) {
        return toResponse(getPlant(id));
    }

    @Transactional
    public PlantResponse create(PlantRequest request) {
        if (plantRepository.existsByNameIgnoreCase(request.name())) {
            throw new DuplicateResourceException("A plant with this name already exists");
        }
        return toResponse(plantRepository.save(apply(new Plant(), request)));
    }

    @Transactional
    public PlantResponse update(Long id, PlantRequest request) {
        Plant plant = getPlant(id);
        plantRepository.findByNameIgnoreCase(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("A plant with this name already exists");
                });
        return toResponse(plantRepository.save(apply(plant, request)));
    }

    @Transactional
    public void delete(Long id) {
        Plant plant = getPlant(id);
        List<PlantDisease> mappings = plantDiseaseRepository.findByPlantId(id);
        plantDiseaseRepository.deleteAll(mappings);
        plantRepository.delete(plant);
    }

    public Plant getPlant(Long id) {
        return plantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plant not found: " + id));
    }

    private Plant apply(Plant plant, PlantRequest request) {
        plant.setName(request.name().trim());
        plant.setScientificName(trimToNull(request.scientificName()));
        plant.setCategory(trimToNull(request.category()));
        plant.setDescription(request.description().trim());
        plant.setGrowingRegion(trimToNull(request.growingRegion()));
        plant.setImageUrl(trimToNull(request.imageUrl()));
        return plant;
    }

    private PlantResponse toResponse(Plant plant) {
        List<PlantResponse.PlantDiseaseSummary> diseases = plantDiseaseRepository.findWithDiseaseByPlantId(plant.getId())
                .stream()
                .map(mapping -> new PlantResponse.PlantDiseaseSummary(
                        mapping.getDisease().getId(),
                        mapping.getDisease().getName(),
                        mapping.getDisease().getSeverity()))
                .toList();
        return PlantResponse.from(plant, diseases);
    }

    private String trimToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
