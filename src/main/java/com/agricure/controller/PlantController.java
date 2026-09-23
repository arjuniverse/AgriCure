package com.agricure.controller;

import com.agricure.dto.plant.PlantResponse;
import com.agricure.service.PlantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    @GetMapping
    public ResponseEntity<List<PlantResponse>> list(@RequestParam(required = false) String search) {
        if (search == null || search.isBlank()) {
            return ResponseEntity.ok(plantService.findAll());
        }
        return ResponseEntity.ok(plantService.search(search.trim()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlantResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(plantService.findById(id));
    }
}
