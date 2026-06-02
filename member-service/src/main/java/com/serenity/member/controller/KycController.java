package com.serenity.member.controller;

import com.serenity.member.dto.KycDocumentRequest;
import com.serenity.member.dto.KycDocumentResponse;
import com.serenity.member.dto.KycVerificationResponse;
import com.serenity.member.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/kyc")
@RequiredArgsConstructor
@Tag(name = "KYC", description = "KYC verification APIs")
public class KycController {

    private final KycService kycService;

    @PostMapping("/{membreId}/initiate")
    @Operation(summary = "Initiate KYC verification for a member")
    public ResponseEntity<KycVerificationResponse> initiateVerification(@PathVariable UUID membreId) {
        log.info("POST /api/v1/kyc/{}/initiate", membreId);
        return ResponseEntity.status(HttpStatus.CREATED).body(kycService.initiateVerification(membreId));
    }

    @PostMapping("/{kycVerificationId}/document")
    @Operation(summary = "Upload a KYC document")
    public ResponseEntity<KycDocumentResponse> uploadDocument(
            @PathVariable UUID kycVerificationId,
            @Valid @RequestBody KycDocumentRequest request) {
        log.info("POST /api/v1/kyc/{}/document", kycVerificationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(kycService.uploadDocument(kycVerificationId, request));
    }

    @PutMapping("/{membreId}/validate")
    @PreAuthorize("hasRole('GESTION_MEMBRES')")
    @Operation(summary = "Validate KYC verification")
    public ResponseEntity<KycVerificationResponse> validate(
            @PathVariable UUID membreId,
            @RequestParam UUID validatedBy) {
        log.info("PUT /api/v1/kyc/{}/validate", membreId);
        return ResponseEntity.ok(kycService.validate(membreId, validatedBy));
    }

    @PutMapping("/{membreId}/reject")
    @PreAuthorize("hasRole('GESTION_MEMBRES')")
    @Operation(summary = "Reject KYC verification")
    public ResponseEntity<KycVerificationResponse> reject(
            @PathVariable UUID membreId,
            @RequestParam UUID rejectedBy,
            @RequestBody Map<String, String> body) {
        log.info("PUT /api/v1/kyc/{}/reject", membreId);
        String motif = body.get("motif");
        return ResponseEntity.ok(kycService.reject(membreId, rejectedBy, motif));
    }

    @GetMapping("/{membreId}")
    @Operation(summary = "Get KYC status for a member")
    public ResponseEntity<KycVerificationResponse> getByMembre(@PathVariable UUID membreId) {
        log.debug("GET /api/v1/kyc/{}", membreId);
        return ResponseEntity.ok(kycService.getByMembre(membreId));
    }
}
