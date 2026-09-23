package com.agricure.controller.admin;

import com.agricure.dto.mapping.PlantDiseaseRequest;
import com.agricure.dto.mapping.PlantDiseaseResponse;
import com.agricure.service.PlantDiseaseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/mappings")
public class AdminMappingController {

    private final PlantDiseaseService plantDiseaseService;

    public AdminMappingController(PlantDiseaseService plantDiseaseService) {
        this.plantDiseaseService = plantDiseaseService;
    }

    @GetMapping
    public ResponseEntity<List<PlantDiseaseResponse>> list() {
        return ResponseEntity.ok(plantDiseaseService.findAll());
    }

    @PostMapping
    public ResponseEntity<PlantDiseaseResponse> create(@Valid @RequestBody PlantDiseaseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantDiseaseService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantDiseaseResponse> update(@PathVariable Long id, @Valid @RequestBody PlantDiseaseRequest request) {
        return ResponseEntity.ok(plantDiseaseService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plantDiseaseService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
