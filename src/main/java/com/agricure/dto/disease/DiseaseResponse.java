package com.agricure.dto.disease;

import com.agricure.entity.Disease;

import java.util.List;

public record DiseaseResponse(
        Long id,
        String name,
        String severity,
        String symptoms,
        String causes,
        String prevention,
        String treatment,
        String symptomKeywords,
        List<AffectedPlant> affectedPlants
) {
    public static DiseaseResponse from(Disease disease, List<AffectedPlant> affectedPlants) {
        return new DiseaseResponse(
                disease.getId(),
                disease.getName(),
                disease.getSeverity(),
                disease.getSymptoms(),
                disease.getCauses(),
                disease.getPrevention(),
                disease.getTreatment(),
                disease.getSymptomKeywords(),
                affectedPlants
        );
    }

    public record AffectedPlant(Long id, String name) {
    }
}
