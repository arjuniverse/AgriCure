package com.agricure.dto.plant;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PlantRequest(
        @NotBlank @Size(max = 120) String name,
        @Size(max = 160) String scientificName,
        @Size(max = 80) String category,
        @NotBlank String description,
        @Size(max = 255) String growingRegion,
        @Size(max = 500) String imageUrl
) {
}
