package com.agricure.dto.identify;

import java.util.List;

public record IdentifyResponse(
        Long plantId,
        String plantName,
        String summary,
        List<DiseaseMatch> matches
) {
    public record DiseaseMatch(
            Long diseaseId,
            String diseaseName,
            String severity,
            int matchPercent,
            List<String> matchedSymptoms,
            String symptoms,
            String causes,
            String prevention,
            String treatment
    ) {
    }
}
