package com.serenity.nanocredit.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.nanocredit.dto.AnnulationRequest;
import com.serenity.nanocredit.dto.EtudeRequest;
import com.serenity.nanocredit.dto.NanoCreditEcheanceResponse;
import com.serenity.nanocredit.dto.NanoCreditGarantResponse;
import com.serenity.nanocredit.dto.NanoCreditRequest;
import com.serenity.nanocredit.dto.NanoCreditResponse;
import com.serenity.nanocredit.dto.NanoCreditVersementResponse;
import com.serenity.nanocredit.dto.RemboursementRequest;
import com.serenity.nanocredit.entity.enums.NanoCreditStatut;
import com.serenity.nanocredit.service.NanoCreditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nano-credits")
@RequiredArgsConstructor
@Tag(name = "Nano-Credit", description = "Nano-credit lifecycle management API")
public class NanoCreditController {

    private final NanoCreditService nanoCreditService;

    @PostMapping
    @Operation(summary = "Create a nano-credit demande")
    public ApiResponse<NanoCreditResponse> createDemande(@Valid @RequestBody NanoCreditRequest request) {
        NanoCreditResponse response = nanoCreditService.createDemande(
                request.getMembreId(),
                request.getPalierId(),
                request.getMontant(),
                request.getWithdrawMode()
        );
        return ApiResponse.created("Nano-credit demande created successfully", response);
    }

    @GetMapping
    @Operation(summary = "List nano-credits with optional filters")
    public ApiResponse<List<NanoCreditResponse>> listNanoCredits(
            @Parameter(description = "Filter by membre ID") @RequestParam(required = false) UUID membreId,
            @Parameter(description = "Filter by statut") @RequestParam(required = false) String statut
    ) {
        List<NanoCreditResponse> responses;

        if (membreId != null && statut != null) {
            NanoCreditStatut creditStatut = NanoCreditStatut.valueOf(statut.toUpperCase());
            responses = nanoCreditService.getByMembreIdAndStatut(membreId, creditStatut);
        } else if (membreId != null) {
            responses = nanoCreditService.getByMembreId(membreId);
        } else if (statut != null) {
            NanoCreditStatut creditStatut = NanoCreditStatut.valueOf(statut.toUpperCase());
            responses = nanoCreditService.getByStatut(creditStatut);
        } else {
            responses = nanoCreditService.getAll();
        }

        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get nano-credit details")
    public ApiResponse<NanoCreditResponse> getNanoCredit(@PathVariable UUID id) {
        NanoCreditResponse response = nanoCreditService.getById(id);
        return ApiResponse.success(response);
    }

    @PutMapping("/{id}/etude")
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Study a nano-credit application (assign AI and human scores)")
    public ApiResponse<NanoCreditResponse> etude(
            @PathVariable UUID id,
            @Valid @RequestBody EtudeRequest request
    ) {
        NanoCreditResponse response = nanoCreditService.etude(id, request.getScoreAi(), request.getScoreHumain());
        return ApiResponse.success("Nano-credit studied successfully", response);
    }

    @PutMapping("/{id}/accorder")
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Approve a nano-credit (creates echeances and caisse accounts)")
    public ApiResponse<NanoCreditResponse> accorder(@PathVariable UUID id) {
        NanoCreditResponse response = nanoCreditService.accorder(id);
        return ApiResponse.success("Nano-credit approved successfully", response);
    }

    @PutMapping("/{id}/debourser")
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Disburse a nano-credit (initiates payment and records accounting)")
    public ApiResponse<NanoCreditResponse> debourser(@PathVariable UUID id) {
        NanoCreditResponse response = nanoCreditService.debourser(id);
        return ApiResponse.success("Nano-credit disbursed successfully", response);
    }

    @PostMapping("/{id}/rembourser")
    @Operation(summary = "Make a repayment on a nano-credit")
    public ApiResponse<NanoCreditVersementResponse> rembourser(
            @PathVariable UUID id,
            @Valid @RequestBody RemboursementRequest request
    ) {
        NanoCreditVersementResponse response = nanoCreditService.rembourser(
                id, request.getEcheanceId(), request.getMontant(), request.getModePaiement()
        );
        return ApiResponse.success("Repayment processed successfully", response);
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Cancel a nano-credit")
    public ApiResponse<NanoCreditResponse> annuler(
            @PathVariable UUID id,
            @RequestBody(required = false) AnnulationRequest request
    ) {
        String motif = request != null ? request.getMotif() : null;
        NanoCreditResponse response = nanoCreditService.annuler(id, motif);
        return ApiResponse.success("Nano-credit cancelled successfully", response);
    }

    @GetMapping("/{id}/echeances")
    @Operation(summary = "List echeances for a nano-credit")
    public ApiResponse<List<NanoCreditEcheanceResponse>> getEcheances(@PathVariable UUID id) {
        List<NanoCreditEcheanceResponse> responses = nanoCreditService.getEcheances(id);
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}/versements")
    @Operation(summary = "List versements for a nano-credit")
    public ApiResponse<List<NanoCreditVersementResponse>> getVersements(@PathVariable UUID id) {
        List<NanoCreditVersementResponse> responses = nanoCreditService.getVersements(id);
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}/garants")
    @Operation(summary = "List garants for a nano-credit")
    public ApiResponse<List<NanoCreditGarantResponse>> getGarants(@PathVariable UUID id) {
        List<NanoCreditGarantResponse> responses = nanoCreditService.getGarants(id);
        return ApiResponse.success(responses);
    }
}
