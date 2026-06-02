package com.serenity.cotisation.controller;

import com.serenity.cotisation.dto.ApiResponse;
import com.serenity.cotisation.dto.VersementDemandeRequest;
import com.serenity.cotisation.dto.VersementDemandeResponse;
import com.serenity.cotisation.enums.VersementDemandeStatut;
import com.serenity.cotisation.service.VersementDemandeService;
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
@RequestMapping("/api/v1/versement-demandes")
@RequiredArgsConstructor
@Tag(name = "Versement Demandes", description = "API de gestion des demandes de versement")
public class VersementDemandeController {

    private final VersementDemandeService versementDemandeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Créer une demande de versement", description = "Soumet une demande de retrait/versement")
    public ResponseEntity<ApiResponse<VersementDemandeResponse>> create(@Valid @RequestBody VersementDemandeRequest request) {
        VersementDemandeResponse response = versementDemandeService.createDemande(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Demande de versement créée avec succès", response));
    }

    @PostMapping("/{id}/traite")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Traiter une demande de versement")
    public ResponseEntity<ApiResponse<VersementDemandeResponse>> traite(
            @PathVariable UUID id,
            @RequestParam UUID traitePar,
            @RequestParam VersementDemandeStatut statut) {
        VersementDemandeResponse response = versementDemandeService.traite(id, traitePar, statut);
        return ResponseEntity.ok(ApiResponse.success("Demande de versement traitée avec succès", response));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Rejeter une demande de versement")
    public ResponseEntity<ApiResponse<VersementDemandeResponse>> reject(
            @PathVariable UUID id,
            @RequestParam UUID traitePar,
            @RequestParam(required = false) String motifRejet) {
        VersementDemandeResponse response = versementDemandeService.reject(id, traitePar, motifRejet);
        return ResponseEntity.ok(ApiResponse.success("Demande de versement rejetée", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Récupérer une demande de versement par ID")
    public ResponseEntity<ApiResponse<VersementDemandeResponse>> getById(@PathVariable UUID id) {
        VersementDemandeResponse response = versementDemandeService.getById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister toutes les demandes de versement")
    public ResponseEntity<ApiResponse<List<VersementDemandeResponse>>> getAll() {
        List<VersementDemandeResponse> responses = versementDemandeService.getAll();
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/membre/{membreId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE', 'MEMBRE')")
    @Operation(summary = "Lister les demandes de versement d'un membre")
    public ResponseEntity<ApiResponse<List<VersementDemandeResponse>>> getByMembre(@PathVariable UUID membreId) {
        List<VersementDemandeResponse> responses = versementDemandeService.getByMembre(membreId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/cotisation/{cotisationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTIONNAIRE')")
    @Operation(summary = "Lister les demandes de versement d'une cotisation")
    public ResponseEntity<ApiResponse<List<VersementDemandeResponse>>> getByCotisation(@PathVariable UUID cotisationId) {
        List<VersementDemandeResponse> responses = versementDemandeService.getByCotisation(cotisationId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }
}
