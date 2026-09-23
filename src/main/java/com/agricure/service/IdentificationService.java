package com.agricure.service;

import com.agricure.dto.identify.IdentifyRequest;
import com.agricure.dto.identify.IdentifyResponse;
import com.agricure.entity.Disease;
import com.agricure.entity.Plant;
import com.agricure.entity.PlantDisease;
import com.agricure.exception.ApiException;
import com.agricure.repository.PlantDiseaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IdentificationService {

    private final PlantService plantService;
    private final PlantDiseaseRepository plantDiseaseRepository;

    public IdentificationService(PlantService plantService, PlantDiseaseRepository plantDiseaseRepository) {
        this.plantService = plantService;
        this.plantDiseaseRepository = plantDiseaseRepository;
    }

    @Transactional(readOnly = true)
    public IdentifyResponse identify(IdentifyRequest request) {
        Plant plant = plantService.getPlant(request.plantId());
        List<String> observed = collectObservedSymptoms(request);
        if (observed.isEmpty()) {
            throw new ApiException("Provide at least one symptom or a short description of what you see");
        }

        List<PlantDisease> mappings = plantDiseaseRepository.findWithDiseaseByPlantId(plant.getId());
        if (mappings.isEmpty()) {
            throw new ApiException("No diseases are currently mapped to " + plant.getName());
        }

        List<IdentifyResponse.DiseaseMatch> matches = mappings.stream()
                .map(mapping -> score(mapping.getDisease(), observed))
                .filter(match -> match.matchPercent() > 0)
                .sorted(Comparator.comparingInt(IdentifyResponse.DiseaseMatch::matchPercent).reversed())
                .toList();

        if (matches.isEmpty()) {
            List<IdentifyResponse.DiseaseMatch> fallback = mappings.stream()
                    .map(mapping -> score(mapping.getDisease(), observed))
                    .sorted(Comparator.comparingInt(IdentifyResponse.DiseaseMatch::matchPercent).reversed())
                    .limit(3)
                    .toList();
            return new IdentifyResponse(
                    plant.getId(),
                    plant.getName(),
                    "No close symptom match was found. Review the common diseases listed for " + plant.getName() + ".",
                    fallback
            );
        }

        String summary = matches.getFirst().matchPercent() >= 60
                ? "Likely match: " + matches.getFirst().diseaseName() + ". Confirm in the field before applying treatment."
                : "Several diseases could explain these symptoms. Compare the top matches before acting.";

        return new IdentifyResponse(plant.getId(), plant.getName(), summary, matches);
    }

    private IdentifyResponse.DiseaseMatch score(Disease disease, List<String> observed) {
        Set<String> catalog = parseKeywords(disease);
        List<String> matched = new ArrayList<>();
        for (String symptom : observed) {
            boolean hit = catalog.stream().anyMatch(keyword ->
                    keyword.contains(symptom) || symptom.contains(keyword) || tokenOverlap(keyword, symptom));
            if (hit) {
                matched.add(symptom);
            }
        }
        int percent = (int) Math.round((matched.size() * 100.0) / observed.size());
        return new IdentifyResponse.DiseaseMatch(
                disease.getId(),
                disease.getName(),
                disease.getSeverity(),
                percent,
                matched,
                disease.getSymptoms(),
                disease.getCauses(),
                disease.getPrevention(),
                disease.getTreatment()
        );
    }

    private Set<String> parseKeywords(Disease disease) {
        String combined = String.join(" ",
                nullToEmpty(disease.getSymptomKeywords()),
                nullToEmpty(disease.getName()),
                nullToEmpty(disease.getSymptoms()),
                nullToEmpty(disease.getCauses()));
        return tokenize(combined);
    }

    private List<String> collectObservedSymptoms(IdentifyRequest request) {
        Set<String> observed = new LinkedHashSet<>();
        if (request.symptoms() != null) {
            request.symptoms().stream()
                    .flatMap(value -> tokenize(value).stream())
                    .filter(token -> token.length() > 2)
                    .forEach(observed::add);
        }
        if (request.description() != null) {
            tokenize(request.description()).stream()
                    .filter(token -> token.length() > 3)
                    .forEach(observed::add);
        }
        return new ArrayList<>(observed);
    }

    private Set<String> tokenize(String value) {
        return Arrays.stream(value.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .map(String::trim)
                .filter(token -> token.length() > 2)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private boolean tokenOverlap(String left, String right) {
        return left.equals(right);
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
