package com.bysmo.serenity.cotisation.controller;

import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.dto.CotisationRequest;
import com.bysmo.serenity.cotisation.dto.CotisationResponse;
import com.bysmo.serenity.cotisation.enums.CotisationType;
import com.bysmo.serenity.cotisation.enums.Visibilite;
import com.bysmo.serenity.cotisation.service.CotisationService;
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
@RequestMapping("/api/v1/cotisations")
@RequiredArgsConstructor
@Tag(name = "Cotisations", description = "API de gestion des cotisations")
public class CotisationController {

    private final CotisationService cotisationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Créer une cotisation", description = "Crée une nouvelle cotisation")
    public ResponseEntity<ApiResponse<CotisationResponse>> create(@Valid @RequestBody CotisationRequest request) {
        CotisationResponse response = cotisationService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Cotisation créée avec succès", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer une cotisation par ID")
    public ResponseEntity<ApiResponse<CotisationResponse>> getById(@PathVariable UUID id) {
        CotisationResponse response = cotisationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister toutes les cotisations")
    public ResponseEntity<ApiResponse<List<CotisationResponse>>> getAll() {
        List<CotisationResponse> responses = cotisationService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/filter")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Filtrer les cotisations", description = "Filtre par actif, type et visibilité")
    public ResponseEntity<ApiResponse<List<CotisationResponse>>> filter(
            @RequestParam(required = false) Boolean actif,
            @RequestParam(required = false) CotisationType type,
            @RequestParam(required = false) Visibilite visibilite) {
        List<CotisationResponse> responses = cotisationService.filter(actif, type, visibilite);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Mettre à jour une cotisation")
    public ResponseEntity<ApiResponse<CotisationResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody CotisationRequest request) {
        CotisationResponse response = cotisationService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cotisation mise à jour avec succès", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer (désactiver) une cotisation")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        cotisationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Cotisation désactivée avec succès", null));
    }
}
