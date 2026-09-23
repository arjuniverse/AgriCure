package com.agricure.dto.plant;

import com.agricure.entity.Plant;

import java.util.List;

public record PlantResponse(
        Long id,
        String name,
        String scientificName,
        String category,
        String description,
        String growingRegion,
        String imageUrl,
        List<PlantDiseaseSummary> diseases
) {
    public static PlantResponse from(Plant plant, List<PlantDiseaseSummary> diseases) {
        return new PlantResponse(
                plant.getId(),
                plant.getName(),
                plant.getScientificName(),
                plant.getCategory(),
                plant.getDescription(),
                plant.getGrowingRegion(),
                plant.getImageUrl(),
                diseases
        );
    }

    public record PlantDiseaseSummary(Long id, String name, String severity) {
    }
}
