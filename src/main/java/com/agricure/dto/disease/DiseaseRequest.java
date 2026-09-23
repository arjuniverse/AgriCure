package com.agricure.dto.disease;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiseaseRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 40) String severity,
        @NotBlank String symptoms,
        @NotBlank String causes,
        @NotBlank String prevention,
        @NotBlank String treatment,
        @Size(max = 500) String symptomKeywords
) {
}
