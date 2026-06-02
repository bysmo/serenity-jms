package com.serenity.nanocredit.controller;

import com.serenity.common.dto.ApiResponse;
import com.serenity.nanocredit.dto.EligibilityRequest;
import com.serenity.nanocredit.dto.EligibilityResponse;
import com.serenity.nanocredit.dto.NanoCreditPalierRequest;
import com.serenity.nanocredit.dto.NanoCreditPalierResponse;
import com.serenity.nanocredit.service.NanoCreditPalierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/nano-credit-paliers")
@RequiredArgsConstructor
@Tag(name = "Nano-Credit Paliers", description = "Nano-credit palier (tier) management API")
public class NanoCreditPalierController {

    private final NanoCreditPalierService palierService;

    @GetMapping
    @Operation(summary = "List all paliers")
    public ApiResponse<List<NanoCreditPalierResponse>> listPaliers(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly
    ) {
        List<NanoCreditPalierResponse> responses;
        if (activeOnly) {
            responses = palierService.getActivePaliers();
        } else {
            responses = palierService.getAllPaliers();
        }
        return ApiResponse.success(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get palier details")
    public ApiResponse<NanoCreditPalierResponse> getPalier(@PathVariable UUID id) {
        NanoCreditPalierResponse response = palierService.getPalierById(id);
        return ApiResponse.success(response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Create a new palier")
    public ApiResponse<NanoCreditPalierResponse> createPalier(
            @Valid @RequestBody NanoCreditPalierRequest request
    ) {
        NanoCreditPalierResponse response = palierService.createPalier(request);
        return ApiResponse.created("Palier created successfully", response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Update a palier")
    public ApiResponse<NanoCreditPalierResponse> updatePalier(
            @PathVariable UUID id,
            @Valid @RequestBody NanoCreditPalierRequest request
    ) {
        NanoCreditPalierResponse response = palierService.updatePalier(id, request);
        return ApiResponse.success("Palier updated successfully", response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_GESTION_NANO_CREDITS')")
    @Operation(summary = "Delete (deactivate) a palier")
    public ApiResponse<Void> deletePalier(@PathVariable UUID id) {
        palierService.deletePalier(id);
        return ApiResponse.success("Palier deactivated successfully", null);
    }

    @PostMapping("/check-eligibility")
    @Operation(summary = "Check member eligibility for a palier")
    public ApiResponse<EligibilityResponse> checkEligibility(
            @Valid @RequestBody EligibilityRequest request
    ) {
        EligibilityResponse response = palierService.checkEligibility(request);
        return ApiResponse.success(response);
    }
}
