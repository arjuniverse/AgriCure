package com.agricure.dto.mapping;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record PlantDiseaseRequest(
        @NotNull Long plantId,
        @NotNull Long diseaseId,
        @Size(max = 255) String notes
) {
}
