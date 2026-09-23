package com.agricure.controller.admin;

import com.agricure.dto.disease.DiseaseRequest;
import com.agricure.dto.disease.DiseaseResponse;
import com.agricure.service.DiseaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/diseases")
public class AdminDiseaseController {

    private final DiseaseService diseaseService;

    public AdminDiseaseController(DiseaseService diseaseService) {
        this.diseaseService = diseaseService;
    }

    @PostMapping
    public ResponseEntity<DiseaseResponse> create(@Valid @RequestBody DiseaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diseaseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiseaseResponse> update(@PathVariable Long id, @Valid @RequestBody DiseaseRequest request) {
        return ResponseEntity.ok(diseaseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        diseaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
