package com.serenity.account.controller;

import com.serenity.account.dto.CaisseBalanceResponse;
import com.serenity.account.dto.CaisseRequest;
import com.serenity.account.dto.CaisseResponse;
import com.serenity.account.dto.JournalCaisseResponse;
import com.serenity.account.entity.enums.CaisseStatut;
import com.serenity.account.entity.enums.CaisseType;
import com.serenity.account.service.CaisseService;
import com.serenity.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class CaisseController {

    private final CaisseService caisseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<CaisseResponse>>> listCaisses(
            @RequestParam(required = false) CaisseType type,
            @RequestParam(required = false) CaisseStatut statut,
            @RequestParam(required = false) UUID membreId) {
        List<CaisseResponse> caisses = caisseService.listCaisses(type, statut, membreId);
        return ResponseEntity.ok(ApiResponse.success(caisses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CaisseBalanceResponse>> getCaisse(@PathVariable UUID id) {
        CaisseBalanceResponse response = caisseService.getCaisseWithBalance(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTION_CAISSES')")
    public ResponseEntity<ApiResponse<CaisseResponse>> createCaisse(
            @Valid @RequestBody CaisseRequest request) {
        CaisseResponse response = caisseService.createCaisse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Caisse créée avec succès", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('GESTION_CAISSES')")
    public ResponseEntity<ApiResponse<CaisseResponse>> updateCaisse(
            @PathVariable UUID id,
            @Valid @RequestBody CaisseRequest request) {
        CaisseResponse response = caisseService.updateCaisse(id, request);
        return ResponseEntity.ok(ApiResponse.success("Caisse mise à jour avec succès", response));
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<ApiResponse<CaisseBalanceResponse>> getBalance(@PathVariable UUID id) {
        CaisseBalanceResponse response = caisseService.getBalance(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{id}/journal")
    public ResponseEntity<ApiResponse<JournalCaisseResponse>> getJournalCaisse(
            @PathVariable UUID id,
            @RequestParam(required = false) LocalDate debut,
            @RequestParam(required = false) LocalDate fin) {
        JournalCaisseResponse response = caisseService.getJournalCaisse(id, debut, fin);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
