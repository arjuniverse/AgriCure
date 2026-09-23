package com.agricure.controller;

import com.agricure.dto.disease.DiseaseResponse;
import com.agricure.service.DiseaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/diseases")
public class DiseaseController {

    private final DiseaseService diseaseService;

    public DiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @GetMapping
    public ResponseEntity<List<DiseaseResponse>> list(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return ResponseEntity.ok(diseaseService.findAll());
        }
        return ResponseEntity.ok(diseaseService.search(search.trim()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiseaseResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(diseaseService.findById(id));
    }
}
