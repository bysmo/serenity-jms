package com.serenity.cotisation.controller;

import com.serenity.cotisation.dto.ApiResponse;
import com.serenity.cotisation.dto.PaiementRequest;
import com.serenity.cotisation.dto.PaiementResponse;
import com.serenity.cotisation.service.PaiementService;
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
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Paiements", description = "API de gestion des paiements")
public class PaiementController {

    private final PaiementService paiementService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Créer un paiement", description = "Enregistre un paiement de cotisation")
    public ResponseEntity<ApiResponse<PaiementResponse>> create(@Valid @RequestBody PaiementRequest request) {
        PaiementResponse response = paiementService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Paiement enregistré avec succès", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer un paiement par ID")
    public ResponseEntity<ApiResponse<PaiementResponse>> getById(@PathVariable UUID id) {
        PaiementResponse response = paiementService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister tous les paiements")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getAll() {
        List<PaiementResponse> responses = paiementService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/membre/{membreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister les paiements d'un membre")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getByMembre(@PathVariable UUID membreId) {
        List<PaiementResponse> responses = paiementService.getByMembre(membreId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/cotisation/{cotisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister les paiements d'une cotisation")
    public ResponseEntity<ApiResponse<List<PaiementResponse>>> getByCotisation(@PathVariable UUID cotisationId) {
        List<PaiementResponse> responses = paiementService.getByCotisation(cotisationId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Annuler un paiement")
    public ResponseEntity<ApiResponse<PaiementResponse>> cancel(@PathVariable UUID id) {
        PaiementResponse response = paiementService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Paiement annulé avec succès", response));
    }
}
