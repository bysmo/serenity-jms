package com.bysmo.serenity.collector.controller;

import com.bysmo.serenity.collector.dto.*;
import com.bysmo.serenity.collector.service.CollecteService;
import com.bysmo.serenity.collector.service.CollecteSessionService;
import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.common.security.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/collector")
@RequiredArgsConstructor
@Slf4j
public class CollectorController {

    private final CollecteSessionService sessionService;
    private final CollecteService collecteService;

    // ==================== Session Endpoints ====================

    /**
     * Opens a new collection session.
     * Only users with the COLLECTEUR role can open sessions.
     */
    @PostMapping("/sessions/open")
    @PreAuthorize("hasRole('COLLECTEUR')")
    public ResponseEntity<ApiResponse<CollecteSessionResponse>> openSession(
            @Valid @RequestBody OpenSessionRequest request) {

        UUID userId = SecurityUtils.getCurrentUserId();
        log.info("REST request to open session for userId={}", userId);

        CollecteSessionResponse response = sessionService.openSession(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Session opened successfully", response));
    }

    /**
     * Gets the active (OUVERT) session for the current collecteur.
     */
    @GetMapping("/sessions/active")
    @PreAuthorize("hasRole('COLLECTEUR')")
    public ResponseEntity<ApiResponse<CollecteSessionResponse>> getActiveSession() {

        UUID userId = SecurityUtils.getCurrentUserId();
        log.info("REST request to get active session for userId={}", userId);

        CollecteSessionResponse response = sessionService.getActiveSession(userId);
        if (response == null) {
            return ResponseEntity.ok(ApiResponse.success("No active session", null));
        }
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Closes an active collection session with reconciliation.
     */
    @PostMapping("/sessions/{sessionId}/close")
    @PreAuthorize("hasRole('COLLECTEUR')")
    public ResponseEntity<ApiResponse<CollecteSessionResponse>> closeSession(
            @PathVariable UUID sessionId) {

        log.info("REST request to close session: sessionId={}", sessionId);

        CollecteSessionResponse response = sessionService.closeSession(sessionId);
        return ResponseEntity.ok(ApiResponse.success("Session closed successfully", response));
    }

    /**
     * Lists sessions for the current collecteur.
     * Accessible by both COLLECTEUR and GESTION_COLLECTE roles.
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('COLLECTEUR', 'GESTION_COLLECTE')")
    public ResponseEntity<ApiResponse<Page<CollecteSessionResponse>>> getSessions(
            @PageableDefault(size = 20) Pageable pageable) {

        UUID userId = SecurityUtils.getCurrentUserId();
        log.info("REST request to list sessions for userId={}", userId);

        Page<CollecteSessionResponse> responses = sessionService.getSessions(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    // ==================== Collecte Endpoints ====================

    /**
     * Creates a new collecte within an active session.
     * Finds the member by phone, generates OTP, records accounting mouvement.
     */
    @PostMapping("/{sessionId}/collect")
    @PreAuthorize("hasRole('COLLECTEUR')")
    public ResponseEntity<ApiResponse<CollecteResponse>> collect(
            @PathVariable UUID sessionId,
            @Valid @RequestBody CollectRequest request) {

        log.info("REST request to create collecte in sessionId={}", sessionId);

        CollecteResponse response = collecteService.collect(sessionId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Collecte created successfully", response));
    }

    /**
     * Confirms a collecte by verifying the OTP code provided by the member.
     */
    @PostMapping("/{collecteId}/confirm")
    @PreAuthorize("hasRole('COLLECTEUR')")
    public ResponseEntity<ApiResponse<CollecteResponse>> confirmCollect(
            @PathVariable UUID collecteId,
            @Valid @RequestBody ConfirmCollectRequest request) {

        log.info("REST request to confirm collecte: collecteId={}", collecteId);

        CollecteResponse response = collecteService.confirmCollect(collecteId, request);
        return ResponseEntity.ok(ApiResponse.success("Collecte confirmed successfully", response));
    }

    /**
     * Lists all collectes for a given session.
     */
    @GetMapping("/{sessionId}/collectes")
    public ResponseEntity<ApiResponse<List<CollecteResponse>>> getBySession(
            @PathVariable UUID sessionId) {

        log.info("REST request to list collectes for sessionId={}", sessionId);

        List<CollecteResponse> responses = collecteService.getBySession(sessionId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Gets the session summary with totals and ecart (discrepancy) calculation.
     */
    @GetMapping("/{sessionId}/summary")
    public ResponseEntity<ApiResponse<SessionSummaryResponse>> getSessionSummary(
            @PathVariable UUID sessionId) {

        log.info("REST request to get session summary for sessionId={}", sessionId);

        SessionSummaryResponse response = sessionService.getSessionSummary(sessionId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
