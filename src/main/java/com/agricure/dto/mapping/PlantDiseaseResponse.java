package com.agricure.dto.mapping;

import com.agricure.entity.PlantDisease;

public record PlantDiseaseResponse(
        Long id,
        Long plantId,
        String plantName,
        Long diseaseId,
        String diseaseName,
        String notes
) {
    public static PlantDiseaseResponse from(PlantDisease mapping) {
        return new PlantDiseaseResponse(
                mapping.getId(),
                mapping.getPlant().getId(),
                mapping.getPlant().getName(),
                mapping.getDisease().getId(),
                mapping.getDisease().getName(),
                mapping.getNotes()
        );
    }
}
