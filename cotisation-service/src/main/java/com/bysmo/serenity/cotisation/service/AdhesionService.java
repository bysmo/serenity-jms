package com.bysmo.serenity.cotisation.service;

import com.bysmo.serenity.cotisation.dto.AdhesionResponse;
import com.bysmo.serenity.cotisation.dto.EngagementRequest;
import com.bysmo.serenity.cotisation.dto.EngagementResponse;
import com.bysmo.serenity.cotisation.entity.Cotisation;
import com.bysmo.serenity.cotisation.entity.CotisationAdhesion;
import com.bysmo.serenity.cotisation.enums.AdhesionStatut;
import com.bysmo.serenity.cotisation.enums.Frequence;
import com.bysmo.serenity.cotisation.event.CotisationEventPublisher;
import com.bysmo.serenity.cotisation.repository.CotisationAdhesionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdhesionService {

    private final CotisationAdhesionRepository adhesionRepository;
    private final CotisationService cotisationService;
    private final EngagementService engagementService;
    private final CotisationEventPublisher eventPublisher;

    @Transactional
    public AdhesionResponse requestAdhesion(UUID membreId, UUID cotisationId) {
        log.info("Requesting adhesion for membreId={}, cotisationId={}", membreId, cotisationId);

        // Verify cotisation exists
        Cotisation cotisation = cotisationService.getEntityById(cotisationId);
        if (!cotisation.getActif()) {
            throw new RuntimeException("La cotisation n'est plus active: " + cotisationId);
        }

        // Check if adhesion already exists
        if (adhesionRepository.existsByMembreIdAndCotisationId(membreId, cotisationId)) {
            CotisationAdhesion existing = adhesionRepository.findByMembreIdAndCotisationId(membreId, cotisationId)
                    .orElseThrow(() -> new RuntimeException("Adhesion existante mais non trouvée"));
            if (existing.getStatut() == AdhesionStatut.ACCEPTE) {
                throw new RuntimeException("Le membre est déjà adhérent à cette cotisation");
            }
            if (existing.getStatut() == AdhesionStatut.EN_ATTENTE) {
                throw new RuntimeException("Une demande d'adhésion est déjà en attente pour cette cotisation");
            }
        }

        CotisationAdhesion adhesion = CotisationAdhesion.builder()
                .cotisationId(cotisationId)
                .membreId(membreId)
                .statut(AdhesionStatut.EN_ATTENTE)
                .build();

        adhesion = adhesionRepository.save(adhesion);
        log.info("Adhesion request created with id={}", adhesion.getId());

        // Publish AdhesionRequestedEvent
        eventPublisher.publishAdhesionRequested(adhesion);

        return mapToResponse(adhesion);
    }

    @Transactional
    public AdhesionResponse accept(UUID adhesionId, UUID traitePar) {
        log.info("Accepting adhesion id={} by {}", adhesionId, traitePar);
        CotisationAdhesion adhesion = adhesionRepository.findById(adhesionId)
                .orElseThrow(() -> new RuntimeException("Adhesion non trouvée avec l'id: " + adhesionId));

        if (adhesion.getStatut() != AdhesionStatut.EN_ATTENTE) {
            throw new RuntimeException("L'adhésion ne peut être acceptée que si elle est en attente. Statut actuel: " + adhesion.getStatut());
        }

        adhesion.setStatut(AdhesionStatut.ACCEPTE);
        adhesion.setTraitePar(traitePar);
        adhesion.setDateTraitement(LocalDateTime.now());
        adhesion = adhesionRepository.save(adhesion);

        // Automatically create an engagement
        Cotisation cotisation = cotisationService.getEntityById(adhesion.getCotisationId());
        EngagementRequest engagementRequest = EngagementRequest.builder()
                .cotisationId(adhesion.getCotisationId())
                .membreId(adhesion.getMembreId())
                .montantEngage(cotisation.getMontant() != null ? cotisation.getMontant() : java.math.BigDecimal.ZERO)
                .periodicite(mapFrequenceToPeriodicite(cotisation.getFrequence()))
                .periodeDebut(LocalDate.now())
                .periodeFin(calculatePeriodeFin(cotisation.getFrequence()))
                .build();
        EngagementResponse engagement = engagementService.create(engagementRequest);
        log.info("Engagement automatically created with id={} for accepted adhesion={}", engagement.getId(), adhesionId);

        return mapToResponse(adhesion);
    }

    @Transactional
    public AdhesionResponse reject(UUID adhesionId, UUID traitePar, String motif) {
        log.info("Rejecting adhesion id={} by {}", adhesionId, traitePar);
        CotisationAdhesion adhesion = adhesionRepository.findById(adhesionId)
                .orElseThrow(() -> new RuntimeException("Adhesion non trouvée avec l'id: " + adhesionId));

        if (adhesion.getStatut() != AdhesionStatut.EN_ATTENTE) {
            throw new RuntimeException("L'adhésion ne peut être refusée que si elle est en attente. Statut actuel: " + adhesion.getStatut());
        }

        adhesion.setStatut(AdhesionStatut.REFUSE);
        adhesion.setTraitePar(traitePar);
        adhesion.setDateTraitement(LocalDateTime.now());
        adhesion.setMotifRefus(motif);
        adhesion = adhesionRepository.save(adhesion);

        log.info("Adhesion rejected with id={}", adhesionId);
        return mapToResponse(adhesion);
    }

    @Transactional(readOnly = true)
    public AdhesionResponse getById(UUID id) {
        log.info("Fetching adhesion by id={}", id);
        CotisationAdhesion adhesion = adhesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhesion non trouvée avec l'id: " + id));
        return mapToResponse(adhesion);
    }

    @Transactional(readOnly = true)
    public List<AdhesionResponse> getByMembre(UUID membreId) {
        log.info("Fetching adhesions for membreId={}", membreId);
        return adhesionRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdhesionResponse> getByCotisation(UUID cotisationId) {
        log.info("Fetching adhesions for cotisationId={}", cotisationId);
        return adhesionRepository.findByCotisationId(cotisationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdhesionResponse> getAll() {
        log.info("Fetching all adhesions");
        return adhesionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Frequence mapFrequenceToPeriodicite(Frequence frequence) {
        return frequence; // Same enum values for periodicite
    }

    private LocalDate calculatePeriodeFin(Frequence frequence) {
        LocalDate now = LocalDate.now();
        return switch (frequence) {
            case MENSUELLE -> now.plusMonths(1);
            case TRIMESTRIELLE -> now.plusMonths(3);
            case SEMESTRIELLE -> now.plusMonths(6);
            case ANNUELLE -> now.plusYears(1);
            case UNIQUE -> now.plusYears(10); // Long-term for unique
        };
    }

    private AdhesionResponse mapToResponse(CotisationAdhesion adhesion) {
        return AdhesionResponse.builder()
                .id(adhesion.getId())
                .cotisationId(adhesion.getCotisationId())
                .membreId(adhesion.getMembreId())
                .statut(adhesion.getStatut())
                .traitePar(adhesion.getTraitePar())
                .dateTraitement(adhesion.getDateTraitement())
                .motifRefus(adhesion.getMotifRefus())
                .createdAt(adhesion.getCreatedAt())
                .updatedAt(adhesion.getUpdatedAt())
                .build();
    }
}
