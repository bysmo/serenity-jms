package com.serenity.collector.service;

import com.serenity.collector.dto.CollecteSessionResponse;
import com.serenity.collector.dto.OpenSessionRequest;
import com.serenity.collector.dto.SessionSummaryResponse;
import com.serenity.collector.entity.CollecteSession;
import com.serenity.collector.entity.enums.SessionStatut;
import com.serenity.collector.event.CollectorEventPublisher;
import com.serenity.collector.repository.CollecteRepository;
import com.serenity.collector.repository.CollecteSessionRepository;
import com.serenity.common.exception.BusinessException;
import com.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollecteSessionService {

    private final CollecteSessionRepository sessionRepository;
    private final CollecteRepository collecteRepository;
    private final CollectorEventPublisher eventPublisher;

    /**
     * Opens a new collection session for a user.
     * A user can only have one active (OUVERT) session at a time.
     *
     * @param userId  the ID of the collecteur (user)
     * @param request the open session request containing montantOuverture
     * @return the created session response
     */
    @Transactional
    public CollecteSessionResponse openSession(UUID userId, OpenSessionRequest request) {
        log.info("Opening new collecte session for userId={}", userId);

        // Check if user already has an active session
        if (sessionRepository.existsByUserIdAndStatut(userId, SessionStatut.OUVERT)) {
            throw new BusinessException("User already has an active collection session", "ACTIVE_SESSION_EXISTS");
        }

        CollecteSession session = CollecteSession.builder()
                .userId(userId)
                .dateSession(LocalDate.now())
                .statut(SessionStatut.OUVERT)
                .montantOuverture(request.getMontantOuverture())
                .openedAt(LocalDateTime.now())
                .build();

        session = sessionRepository.save(session);
        log.info("Collecte session opened successfully: sessionId={}", session.getId());

        return mapToSessionResponse(session);
    }

    /**
     * Closes an active collection session.
     * Computes montant_fermeture, compares with total confirmed collectes (ecart),
     * and publishes a CollectorSessionClosedEvent.
     *
     * @param sessionId the ID of the session to close
     * @return the updated session response
     */
    @Transactional
    public CollecteSessionResponse closeSession(UUID sessionId) {
        log.info("Closing collecte session: sessionId={}", sessionId);

        CollecteSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("CollecteSession", sessionId));

        if (session.getStatut() == SessionStatut.FERME) {
            throw new BusinessException("Session is already closed", "SESSION_ALREADY_CLOSED");
        }

        // Calculate total confirmed collectes
        BigDecimal totalCollectes = collecteRepository.sumConfirmedBySessionId(sessionId);

        // Set montant_fermeture to the total of confirmed collectes + montant_ouverture
        BigDecimal montantFermeture = session.getMontantOuverture().add(totalCollectes);

        session.setStatut(SessionStatut.FERME);
        session.setMontantFermeture(montantFermeture);
        session.setClosedAt(LocalDateTime.now());

        session = sessionRepository.save(session);

        // Compute ecart for logging
        BigDecimal ecart = montantFermeture.subtract(session.getMontantOuverture()).subtract(totalCollectes);
        log.info("Session closed: sessionId={}, montantOuverture={}, montantFermeture={}, totalCollectes={}, ecart={}",
                session.getId(), session.getMontantOuverture(), montantFermeture, totalCollectes, ecart);

        // Publish session closed event
        eventPublisher.publishSessionClosed(session);

        return mapToSessionResponse(session);
    }

    /**
     * Gets the active (OUVERT) session for a user.
     *
     * @param userId the collecteur's user ID
     * @return the active session response, or null if no active session
     */
    @Transactional(readOnly = true)
    public CollecteSessionResponse getActiveSession(UUID userId) {
        log.debug("Fetching active session for userId={}", userId);

        return sessionRepository.findByUserIdAndStatut(userId, SessionStatut.OUVERT)
                .map(this::mapToSessionResponse)
                .orElse(null);
    }

    /**
     * Gets a paginated list of sessions for a user.
     *
     * @param userId   the collecteur's user ID
     * @param pageable pagination parameters
     * @return a page of session responses
     */
    @Transactional(readOnly = true)
    public Page<CollecteSessionResponse> getSessions(UUID userId, Pageable pageable) {
        log.debug("Fetching sessions for userId={}, page={}", userId, pageable.getPageNumber());

        return sessionRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToSessionResponse);
    }

    /**
     * Gets the session summary including total collectes and ecart.
     *
     * @param sessionId the session ID
     * @return the session summary response
     */
    @Transactional(readOnly = true)
    public SessionSummaryResponse getSessionSummary(UUID sessionId) {
        log.debug("Fetching session summary for sessionId={}", sessionId);

        CollecteSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException("CollecteSession", sessionId));

        BigDecimal totalCollectes = collecteRepository.sumConfirmedBySessionId(sessionId);
        long nombreCollectes = collecteRepository.countByCollecteSessionId(sessionId);

        BigDecimal montantFermeture = session.getMontantFermeture() != null
                ? session.getMontantFermeture()
                : BigDecimal.ZERO;

        // Ecart = montant_fermeture - (montant_ouverture + total_collectes)
        BigDecimal expectedTotal = session.getMontantOuverture().add(totalCollectes);
        BigDecimal ecart = montantFermeture.subtract(expectedTotal);

        return SessionSummaryResponse.builder()
                .sessionId(session.getId())
                .dateSession(session.getDateSession())
                .montantOuverture(session.getMontantOuverture())
                .montantFermeture(montantFermeture)
                .totalCollectes(totalCollectes)
                .nombreCollectes(nombreCollectes)
                .ecart(ecart)
                .openedAt(session.getOpenedAt())
                .closedAt(session.getClosedAt())
                .build();
    }

    private CollecteSessionResponse mapToSessionResponse(CollecteSession session) {
        return CollecteSessionResponse.builder()
                .id(session.getId())
                .userId(session.getUserId())
                .dateSession(session.getDateSession())
                .statut(session.getStatut().name())
                .montantOuverture(session.getMontantOuverture())
                .montantFermeture(session.getMontantFermeture())
                .openedAt(session.getOpenedAt())
                .closedAt(session.getClosedAt())
                .createdAt(session.getCreatedAt())
                .build();
    }
}
