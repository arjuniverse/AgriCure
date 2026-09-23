package com.agricure.controller.admin;

import com.agricure.dto.plant.PlantRequest;
import com.agricure.dto.plant.PlantResponse;
import com.agricure.service.PlantService;
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
@RequestMapping("/api/admin/plants")
public class AdminPlantController {

    private final PlantService plantService;

    public AdminPlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @PostMapping
    public ResponseEntity<PlantResponse> create(@Valid @RequestBody PlantRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(plantService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlantResponse> update(@PathVariable Long id, @Valid @RequestBody PlantRequest request) {
        return ResponseEntity.ok(plantService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        plantService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
