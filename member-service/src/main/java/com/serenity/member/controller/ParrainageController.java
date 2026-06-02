package com.serenity.member.controller;

import com.serenity.member.dto.ParrainageCommissionResponse;
import com.serenity.member.dto.ParrainageConfigRequest;
import com.serenity.member.dto.ParrainageConfigResponse;
import com.serenity.member.service.ParrainageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/parrainage")
@RequiredArgsConstructor
@Tag(name = "Parrainage", description = "Referral system APIs")
public class ParrainageController {

    private final ParrainageService parrainageService;

    @GetMapping("/config")
    @Operation(summary = "Get current parrainage configuration")
    public ResponseEntity<ParrainageConfigResponse> getConfig() {
        log.debug("GET /api/v1/parrainage/config");
        return ResponseEntity.ok(parrainageService.getConfig());
    }

    @PutMapping("/config")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Update parrainage configuration")
    public ResponseEntity<ParrainageConfigResponse> updateConfig(
            @Valid @RequestBody ParrainageConfigRequest request) {
        log.info("PUT /api/v1/parrainage/config");
        return ResponseEntity.ok(parrainageService.updateConfig(request));
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply a referral code")
    public ResponseEntity<Map<String, Object>> applyReferralCode(@RequestBody Map<String, Object> body) {
        log.info("POST /api/v1/parrainage/apply");
        UUID filleulId = UUID.fromString(body.get("filleulId").toString());
        String codeParrainage = body.get("codeParrainage").toString();
        parrainageService.processReferral(filleulId, codeParrainage);
        return ResponseEntity.ok(Map.of(
                "message", "Code de parrainage appliqué avec succès",
                "filleulId", filleulId
        ));
    }

    @GetMapping("/commissions/{parrainId}")
    @Operation(summary = "Get commissions for a parrain")
    public ResponseEntity<List<ParrainageCommissionResponse>> getCommissionsByParrain(
            @PathVariable UUID parrainId) {
        log.debug("GET /api/v1/parrainage/commissions/{}", parrainId);
        return ResponseEntity.ok(parrainageService.getCommissionsByParrain(parrainId));
    }

    @PostMapping("/commissions/{id}/claim")
    @Operation(summary = "Claim a commission")
    public ResponseEntity<ParrainageCommissionResponse> claimCommission(@PathVariable UUID id) {
        log.info("POST /api/v1/parrainage/commissions/{}/claim", id);
        return ResponseEntity.ok(parrainageService.claimCommission(id));
    }
}
