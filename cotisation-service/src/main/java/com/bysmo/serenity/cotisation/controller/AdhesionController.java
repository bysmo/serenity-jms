package com.bysmo.serenity.cotisation.controller;

import com.bysmo.serenity.cotisation.dto.AdhesionRequest;
import com.bysmo.serenity.cotisation.dto.AdhesionResponse;
import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.service.AdhesionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/adhesions")
@RequiredArgsConstructor
@Tag(name = "Adhésions", description = "API de gestion des adhésions aux cotisations")
public class AdhesionController {

    private final AdhesionService adhesionService;

    @PostMapping("/request")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Demander une adhésion", description = "Crée une demande d'adhésion à une cotisation")
    public ResponseEntity<ApiResponse<AdhesionResponse>> requestAdhesion(@Valid @RequestBody AdhesionRequest request) {
        AdhesionResponse response = adhesionService.requestAdhesion(request.getMembreId(), request.getCotisationId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Demande d'adhésion créée avec succès", response));
    }

    @PostMapping("/{id}/accept")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Accepter une adhésion", description = "Accepte une demande d'adhésion et crée automatiquement un engagement")
    public ResponseEntity<ApiResponse<AdhesionResponse>> accept(
            @PathVariable UUID id,
            @RequestParam UUID traitePar) {
        AdhesionResponse response = adhesionService.accept(id, traitePar);
        return ResponseEntity.ok(ApiResponse.success("Adhésion acceptée avec succès", response));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Refuser une adhésion")
    public ResponseEntity<ApiResponse<AdhesionResponse>> reject(
            @PathVariable UUID id,
            @RequestParam UUID traitePar,
            @RequestParam(required = false) String motif) {
        AdhesionResponse response = adhesionService.reject(id, traitePar, motif);
        return ResponseEntity.ok(ApiResponse.success("Adhésion refusée", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer une adhésion par ID")
    public ResponseEntity<ApiResponse<AdhesionResponse>> getById(@PathVariable UUID id) {
        AdhesionResponse response = adhesionService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister toutes les adhésions")
    public ResponseEntity<ApiResponse<List<AdhesionResponse>>> getAll() {
        List<AdhesionResponse> responses = adhesionService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/membre/{membreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister les adhésions d'un membre")
    public ResponseEntity<ApiResponse<List<AdhesionResponse>>> getByMembre(@PathVariable UUID membreId) {
        List<AdhesionResponse> responses = adhesionService.getByMembre(membreId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/cotisation/{cotisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister les adhésions d'une cotisation")
    public ResponseEntity<ApiResponse<List<AdhesionResponse>>> getByCotisation(@PathVariable UUID cotisationId) {
        List<AdhesionResponse> responses = adhesionService.getByCotisation(cotisationId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
