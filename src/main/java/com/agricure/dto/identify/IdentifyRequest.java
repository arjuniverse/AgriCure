package com.agricure.dto.identify;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record IdentifyRequest(
        @NotNull Long plantId,
        List<String> symptoms,
        String description
) {
}
