package com.serenity.cotisation.controller;

import com.serenity.cotisation.dto.ApiResponse;
import com.serenity.cotisation.dto.RemboursementRequest;
import com.serenity.cotisation.dto.RemboursementResponse;
import com.serenity.cotisation.service.RemboursementService;
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
@RequestMapping("/api/v1/refunds")
@RequiredArgsConstructor
@Tag(name = "Remboursements", description = "API de gestion des remboursements")
public class RemboursementController {

    private final RemboursementService remboursementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Créer un remboursement", description = "Crée une demande de remboursement et enregistre le mouvement SORTIE")
    public ResponseEntity<ApiResponse<RemboursementResponse>> create(@Valid @RequestBody RemboursementRequest request) {
        RemboursementResponse response = remboursementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Remboursement créé avec succès", response));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un remboursement")
    public ResponseEntity<ApiResponse<RemboursementResponse>> approve(
            @PathVariable UUID id,
            @RequestParam UUID traitePar) {
        RemboursementResponse response = remboursementService.approve(id, traitePar);
        return ResponseEntity.ok(ApiResponse.success("Remboursement approuvé avec succès", response));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Refuser un remboursement")
    public ResponseEntity<ApiResponse<RemboursementResponse>> reject(
            @PathVariable UUID id,
            @RequestParam UUID traitePar,
            @RequestParam(required = false) String commentaire) {
        RemboursementResponse response = remboursementService.reject(id, traitePar, commentaire);
        return ResponseEntity.ok(ApiResponse.success("Remboursement refusé", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer un remboursement par ID")
    public ResponseEntity<ApiResponse<RemboursementResponse>> getById(@PathVariable UUID id) {
        RemboursementResponse response = remboursementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister tous les remboursements")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getAll() {
        List<RemboursementResponse> responses = remboursementService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/membre/{membreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister les remboursements d'un membre")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getByMembre(@PathVariable UUID membreId) {
        List<RemboursementResponse> responses = remboursementService.getByMembre(membreId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/cotisation/{cotisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister les remboursements d'une cotisation")
    public ResponseEntity<ApiResponse<List<RemboursementResponse>>> getByCotisation(@PathVariable UUID cotisationId) {
        List<RemboursementResponse> responses = remboursementService.getByCotisation(cotisationId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
