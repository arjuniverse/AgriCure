package com.agricure.controller;

import com.agricure.dto.identify.IdentifyRequest;
import com.agricure.dto.identify.IdentifyResponse;
import com.agricure.service.IdentificationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/identify")
public class IdentificationController {

    private final IdentificationService identificationService;

    public IdentificationController(IdentificationService identificationService) {
        this.identificationService = identificationService;
    }

    @PostMapping
    public ResponseEntity<IdentifyResponse> identify(@Valid @RequestBody IdentifyRequest request) {
        return ResponseEntity.ok(identificationService.identify(request));
    }
}
