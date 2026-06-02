package com.bysmo.serenity.collector.service;

import com.bysmo.serenity.collector.client.AccountServiceClient;
import com.bysmo.serenity.collector.client.MemberServiceClient;
import com.bysmo.serenity.collector.client.dto.AccountingEntryRequest;
import com.bysmo.serenity.collector.client.dto.MembreSummaryResponse;
import com.bysmo.serenity.collector.client.dto.MouvementCaisseResponse;
import com.bysmo.serenity.collector.dto.CollectRequest;
import com.bysmo.serenity.collector.dto.CollecteResponse;
import com.bysmo.serenity.collector.dto.ConfirmCollectRequest;
import com.bysmo.serenity.collector.entity.Collecte;
import com.bysmo.serenity.collector.entity.CollecteSession;
import com.bysmo.serenity.collector.entity.enums.SessionStatut;
import com.bysmo.serenity.collector.entity.enums.TypeCollecte;
import com.bysmo.serenity.collector.repository.CollecteRepository;
import com.bysmo.serenity.collector.repository.CollecteSessionRepository;
import com.bysmo.serenity.common.dto.ApiResponse;
import com.bysmo.serenity.common.exception.BusinessException;
import com.bysmo.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollecteService {

    private final CollecteRepository collecteRepository;
    private final CollecteSessionRepository sessionRepository;
    private final MemberServiceClient memberServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final OtpService otpService;

    /**
     * Creates a new collecte (field collection) entry within a session.
     * - Finds the member by phone number via member-service (Feign)
     * - Generates an OTP code for the collecte
     * - Records an ENTREE mouvement in account-service (Feign)
     *
     * @param sessionId the active session ID
     * @param request   the collect request containing telephone, typeCollecte, montant, etc.
     * @return the created collecte response
     */
    @Transactional
    public CollecteResponse collect(UUID sessionId, CollectRequest request) {
        log.info("Creating collecte for sessionId={}, telephone={}", sessionId, request.getTelephone());

        // Validate session exists and is open
        CollecteSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("CollecteSession", sessionId));

        if (session.getStatut() != SessionStatut.OUVERT) {
            throw new BusinessException("Cannot collect in a closed session", "SESSION_NOT_OPEN");
        }

        // Find member by phone via member-service
        MembreSummaryResponse membre = findMemberByTelephone(request.getTelephone());

        // Generate OTP code
        String otpCode = otpService.generateOtp();

        // Parse typeCollecte
        TypeCollecte typeCollecte;
        try {
            typeCollecte = TypeCollecte.valueOf(request.getTypeCollecte());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Invalid typeCollecte: " + request.getTypeCollecte(), "INVALID_TYPE_COLLECTE");
        }

        // Generate reference transaction
        String referenceTransaction = "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create collecte entity
        Collecte collecte = Collecte.builder()
                .collecteSessionId(sessionId)
                .membreId(membre.getId())
                .typeCollecte(typeCollecte)
                .montant(request.getMontant())
                .echeanceType(request.getEcheanceType())
                .echeanceId(request.getEcheanceId())
                .otpCode(otpCode)
                .isConfirmed(false)
                .referenceTransaction(referenceTransaction)
                .build();

        collecte = collecteRepository.save(collecte);
        log.info("Collecte created: collecteId={}, otpCode generated, refTxn={}",
                collecte.getId(), referenceTransaction);

        // Record ENTREE mouvement in account-service
        recordCollecteMouvement(session, collecte, membre);

        return mapToCollecteResponse(collecte);
    }

    /**
     * Confirms a collecte by verifying the OTP code.
     *
     * @param collecteId the collecte ID to confirm
     * @param request    the confirm request containing the OTP code
     * @return the updated collecte response
     */
    @Transactional
    public CollecteResponse confirmCollect(UUID collecteId, ConfirmCollectRequest request) {
        log.info("Confirming collecte: collecteId={}", collecteId);

        Collecte collecte = collecteRepository.findById(collecteId)
                .orElseThrow(() -> new EntityNotFoundException("Collecte", collecteId));

        if (collecte.getIsConfirmed()) {
            throw new BusinessException("Collecte is already confirmed", "COLLECTE_ALREADY_CONFIRMED");
        }

        // Verify OTP
        if (!otpService.verifyOtp(request.getOtpCode(), collecte.getOtpCode())) {
            throw new BusinessException("Invalid OTP code", "INVALID_OTP");
        }

        collecte.setIsConfirmed(true);
        collecte.setConfirmedAt(LocalDateTime.now());

        collecte = collecteRepository.save(collecte);
        log.info("Collecte confirmed successfully: collecteId={}", collecte.getId());

        return mapToCollecteResponse(collecte);
    }

    /**
     * Gets all collectes for a given session.
     *
     * @param sessionId the session ID
     * @return list of collecte responses
     */
    @Transactional(readOnly = true)
    public List<CollecteResponse> getBySession(UUID sessionId) {
        log.debug("Fetching collectes for sessionId={}", sessionId);

        return collecteRepository.findByCollecteSessionIdOrderByCreatedAtDesc(sessionId)
                .stream()
                .map(this::mapToCollecteResponse)
                .toList();
    }

    /**
     * Gets the total amount of confirmed collectes for a session.
     *
     * @param sessionId the session ID
     * @return the total confirmed amount
     */
    @Transactional(readOnly = true)
    public BigDecimal getTotalCollected(UUID sessionId) {
        log.debug("Computing total collected for sessionId={}", sessionId);
        return collecteRepository.sumConfirmedBySessionId(sessionId);
    }

    /**
     * Finds a member by telephone number using the member-service Feign client.
     */
    private MembreSummaryResponse findMemberByTelephone(String telephone) {
        try {
            ApiResponse<MembreSummaryResponse> response = memberServiceClient.searchByTelephone(telephone);
            if (response == null || response.getData() == null) {
                throw new BusinessException("Member not found with telephone: " + telephone, "MEMBER_NOT_FOUND");
            }
            return response.getData();
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw e;
            }
            log.error("Error calling member-service for telephone={}: {}", telephone, e.getMessage());
            throw new BusinessException("Failed to lookup member by telephone", "MEMBER_SERVICE_ERROR", e);
        }
    }

    /**
     * Records an ENTREE mouvement in the account-service for the collecte.
     */
    private void recordCollecteMouvement(CollecteSession session, Collecte collecte, MembreSummaryResponse membre) {
        try {
            AccountingEntryRequest entry = AccountingEntryRequest.builder()
                    .caisseId(null) // Will be determined by account-service
                    .montant(collecte.getMontant())
                    .sens("ENTREE")
                    .type("COLLECTE")
                    .description("Collecte " + collecte.getTypeCollecte().name() + " - Membre " + membre.getNom() + " " + membre.getPrenom())
                    .referenceType("COLLECTE")
                    .referenceId(collecte.getId())
                    .build();

            ApiResponse<MouvementCaisseResponse> response = accountServiceClient.recordMouvement(entry);
            if (response != null && response.getData() != null) {
                log.info("Mouvement recorded successfully: mouvementId={}", response.getData().getId());
            } else {
                log.warn("Account-service returned no data for collecte mouvement");
            }
        } catch (Exception e) {
            log.error("Error recording mouvement in account-service for collecteId={}: {}", collecte.getId(), e.getMessage());
            // Don't fail the collecte creation if accounting fails - it can be reconciled later
        }
    }

    private CollecteResponse mapToCollecteResponse(Collecte collecte) {
        return CollecteResponse.builder()
                .id(collecte.getId())
                .collecteSessionId(collecte.getCollecteSessionId())
                .membreId(collecte.getMembreId())
                .typeCollecte(collecte.getTypeCollecte().name())
                .montant(collecte.getMontant())
                .echeanceType(collecte.getEcheanceType())
                .echeanceId(collecte.getEcheanceId())
                .isConfirmed(collecte.getIsConfirmed())
                .confirmedAt(collecte.getConfirmedAt())
                .referenceTransaction(collecte.getReferenceTransaction())
                .createdAt(collecte.getCreatedAt())
                .build();
    }
}
