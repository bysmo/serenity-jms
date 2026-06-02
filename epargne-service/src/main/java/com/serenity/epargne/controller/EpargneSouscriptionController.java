package com.serenity.epargne.controller;

import com.serenity.epargne.dto.SouscriptionRequest;
import com.serenity.epargne.entity.EpargneEcheance;
import com.serenity.epargne.entity.EpargneSouscription;
import com.serenity.epargne.entity.EpargneVersement;
import com.serenity.epargne.service.EpargneEcheanceService;
import com.serenity.epargne.service.EpargneSouscriptionService;
import com.serenity.epargne.service.EpargneVersementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/epargne")
@RequiredArgsConstructor
@Tag(name = "Epargne Souscriptions", description = "Gestion des souscriptions épargne")
public class EpargneSouscriptionController {

    private final EpargneSouscriptionService souscriptionService;
    private final EpargneEcheanceService echeanceService;
    private final EpargneVersementService versementService;

    @PostMapping("/subscribe")
    @Operation(summary = "Souscrire à un plan d'épargne")
    public ResponseEntity<EpargneSouscription> subscribe(
            @Valid @RequestBody SouscriptionRequest request) {
        EpargneSouscription souscription = souscriptionService.souscrire(
                request.getMembreId(),
                request.getPlanId(),
                request.getMontant()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(souscription);
    }

    @GetMapping("/membre/{membreId}")
    @Operation(summary = "Lister les souscriptions d'un membre")
    public ResponseEntity<List<EpargneSouscription>> getByMembre(@PathVariable UUID membreId) {
        return ResponseEntity.ok(souscriptionService.getByMembre(membreId));
    }

    @GetMapping("/plan/{planId}")
    @Operation(summary = "Lister les souscriptions d'un plan")
    public ResponseEntity<List<EpargneSouscription>> getByPlan(@PathVariable UUID planId) {
        return ResponseEntity.ok(souscriptionService.getByPlan(planId));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Annuler une souscription")
    public ResponseEntity<EpargneSouscription> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(souscriptionService.annuler(id));
    }

    @GetMapping("/{id}/echeances")
    @Operation(summary = "Lister les échéances d'une souscription")
    public ResponseEntity<List<EpargneEcheance>> getEcheances(@PathVariable UUID id) {
        return ResponseEntity.ok(echeanceService.getBySouscription(id));
    }

    @GetMapping("/{id}/versements")
    @Operation(summary = "Lister les versements d'une souscription")
    public ResponseEntity<List<EpargneVersement>> getVersements(@PathVariable UUID id) {
        return ResponseEntity.ok(versementService.getBySouscription(id));
    }
}
