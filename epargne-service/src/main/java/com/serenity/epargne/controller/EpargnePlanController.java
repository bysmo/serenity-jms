package com.serenity.epargne.controller;

import com.serenity.epargne.dto.EpargnePlanDto;
import com.serenity.epargne.entity.EpargnePlan;
import com.serenity.epargne.service.EpargnePlanService;
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
@RequestMapping("/api/v1/epargne-plans")
@RequiredArgsConstructor
@Tag(name = "Epargne Plans", description = "Gestion des plans d'épargne")
public class EpargnePlanController {

    private final EpargnePlanService planService;

    @GetMapping
    @PreAuthorize("hasAuthority('GESTION_EPARGNE')")
    @Operation(summary = "Lister tous les plans d'épargne")
    public ResponseEntity<List<EpargnePlan>> getAll() {
        return ResponseEntity.ok(planService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('GESTION_EPARGNE')")
    @Operation(summary = "Créer un nouveau plan d'épargne")
    public ResponseEntity<EpargnePlan> create(@Valid @RequestBody EpargnePlanDto.CreateRequest request) {
        EpargnePlan plan = planService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(plan);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTION_EPARGNE')")
    @Operation(summary = "Mettre à jour un plan d'épargne")
    public ResponseEntity<EpargnePlan> update(@PathVariable UUID id,
                                               @Valid @RequestBody EpargnePlanDto.UpdateRequest request) {
        return ResponseEntity.ok(planService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('GESTION_EPARGNE')")
    @Operation(summary = "Supprimer un plan d'épargne")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        planService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/toggle")
    @PreAuthorize("hasAuthority('GESTION_EPARGNE')")
    @Operation(summary = "Activer/désactiver un plan d'épargne")
    public ResponseEntity<EpargnePlan> toggleActive(@PathVariable UUID id,
                                                     @RequestParam boolean active) {
        return ResponseEntity.ok(planService.toggleActive(id, active));
    }
}
