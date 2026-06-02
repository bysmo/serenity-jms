package com.bysmo.serenity.cotisation.controller;

import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.dto.EngagementRequest;
import com.bysmo.serenity.cotisation.dto.EngagementResponse;
import com.bysmo.serenity.cotisation.enums.EngagementStatut;
import com.bysmo.serenity.cotisation.service.EngagementService;
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
@RequestMapping("/api/v1/engagements")
@RequiredArgsConstructor
@Tag(name = "Engagements", description = "API de gestion des engagements")
public class EngagementController {

    private final EngagementService engagementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Créer un engagement", description = "Crée un nouvel engagement de cotisation")
    public ResponseEntity<ApiResponse<EngagementResponse>> create(@Valid @RequestBody EngagementRequest request) {
        EngagementResponse response = engagementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Engagement créé avec succès", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer un engagement par ID")
    public ResponseEntity<ApiResponse<EngagementResponse>> getById(@PathVariable UUID id) {
        EngagementResponse response = engagementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister tous les engagements")
    public ResponseEntity<ApiResponse<List<EngagementResponse>>> getAll() {
        List<EngagementResponse> responses = engagementService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/membre/{membreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister les engagements d'un membre")
    public ResponseEntity<ApiResponse<List<EngagementResponse>>> getByMembre(@PathVariable UUID membreId) {
        List<EngagementResponse> responses = engagementService.getByMembre(membreId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/cotisation/{cotisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister les engagements d'une cotisation")
    public ResponseEntity<ApiResponse<List<EngagementResponse>>> getByCotisation(@PathVariable UUID cotisationId) {
        List<EngagementResponse> responses = engagementService.getByCotisation(cotisationId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}/statut")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Mettre à jour le statut d'un engagement")
    public ResponseEntity<ApiResponse<EngagementResponse>> updateStatut(
            @PathVariable UUID id,
            @RequestParam EngagementStatut statut) {
        EngagementResponse response = engagementService.updateStatut(id, statut);
        return ResponseEntity.ok(ApiResponse.success("Statut de l'engagement mis à jour", response));
    }
}
