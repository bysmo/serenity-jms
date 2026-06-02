package com.serenity.epargne.controller;

import com.serenity.epargne.dto.VersementRequest;
import com.serenity.epargne.entity.EpargneVersement;
import com.serenity.epargne.service.EpargneVersementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/epargne/versements")
@RequiredArgsConstructor
@Tag(name = "Epargne Versements", description = "Gestion des versements épargne")
public class EpargneVersementController {

    private final EpargneVersementService versementService;

    @PostMapping
    @Operation(summary = "Effectuer un versement")
    public ResponseEntity<EpargneVersement> verse(
            @Valid @RequestBody VersementRequest request) {
        EpargneVersement versement = versementService.verse(
                request.getSouscriptionId(),
                request.getEcheanceId(),
                request.getMontant(),
                request.getModePaiement()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(versement);
    }

    @GetMapping("/souscription/{souscriptionId}")
    @Operation(summary = "Lister les versements d'une souscription")
    public ResponseEntity<List<EpargneVersement>> getBySouscription(@PathVariable UUID souscriptionId) {
        return ResponseEntity.ok(versementService.getBySouscription(souscriptionId));
    }

    @GetMapping("/souscription/{souscriptionId}/total")
    @Operation(summary = "Obtenir le total des versements d'une souscription")
    public ResponseEntity<BigDecimal> getTotalVerse(@PathVariable UUID souscriptionId) {
        return ResponseEntity.ok(versementService.getTotalVerse(souscriptionId));
    }
}
