package com.bysmo.serenity.cotisation.service;

import com.bysmo.serenity.cotisation.client.MemberServiceClient;
import com.bysmo.serenity.cotisation.client.dto.MembreSummaryResponse;
import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.dto.EngagementRequest;
import com.bysmo.serenity.cotisation.dto.EngagementResponse;
import com.bysmo.serenity.cotisation.entity.Cotisation;
import com.bysmo.serenity.cotisation.entity.Engagement;
import com.bysmo.serenity.cotisation.enums.EngagementStatut;
import com.bysmo.serenity.cotisation.repository.EngagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EngagementService {

    private final EngagementRepository engagementRepository;
    private final CotisationService cotisationService;
    private final MemberServiceClient memberServiceClient;

    @Transactional
    public EngagementResponse create(EngagementRequest request) {
        log.info("Creating engagement for membreId={}, cotisationId={}", request.getMembreId(), request.getCotisationId());

        // Verify member exists via Feign
        try {
            ApiResponse<MembreSummaryResponse> memberResponse = memberServiceClient.getMemberById(request.getMembreId());
            if (memberResponse == null || !memberResponse.isSuccess() || memberResponse.getData() == null) {
                throw new RuntimeException("Membre non trouvé avec l'id: " + request.getMembreId());
            }
            log.info("Member verified: {} {}", memberResponse.getData().getNom(), memberResponse.getData().getPrenom());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying member with id={}: {}", request.getMembreId(), e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification du membre: " + e.getMessage());
        }

        // Verify cotisation exists
        Cotisation cotisation = cotisationService.getEntityById(request.getCotisationId());
        if (!cotisation.getActif()) {
            throw new RuntimeException("La cotisation n'est plus active: " + request.getCotisationId());
        }

        Engagement engagement = Engagement.builder()
                .cotisationId(request.getCotisationId())
                .membreId(request.getMembreId())
                .montantEngage(request.getMontantEngage())
                .montantPaye(java.math.BigDecimal.ZERO)
                .periodicite(request.getPeriodicite())
                .periodeDebut(request.getPeriodeDebut())
                .periodeFin(request.getPeriodeFin())
                .statut(EngagementStatut.EN_COURS)
                .tag(request.getTag())
                .build();

        engagement = engagementRepository.save(engagement);
        log.info("Engagement created with id={}", engagement.getId());
        return mapToResponse(engagement);
    }

    @Transactional(readOnly = true)
    public EngagementResponse getById(UUID id) {
        log.info("Fetching engagement by id={}", id);
        Engagement engagement = engagementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engagement non trouvé avec l'id: " + id));
        return mapToResponse(engagement);
    }

    @Transactional(readOnly = true)
    public Engagement getEntityById(UUID id) {
        return engagementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engagement non trouvé avec l'id: " + id));
    }

    @Transactional(readOnly = true)
    public List<EngagementResponse> getByMembre(UUID membreId) {
        log.info("Fetching engagements for membreId={}", membreId);
        return engagementRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EngagementResponse> getByCotisation(UUID cotisationId) {
        log.info("Fetching engagements for cotisationId={}", cotisationId);
        return engagementRepository.findByCotisationId(cotisationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EngagementResponse> getAll() {
        log.info("Fetching all engagements");
        return engagementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EngagementResponse updateStatut(UUID id, EngagementStatut statut) {
        log.info("Updating engagement id={} to statut={}", id, statut);
        Engagement engagement = engagementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Engagement non trouvé avec l'id: " + id));
        engagement.setStatut(statut);
        engagement = engagementRepository.save(engagement);
        log.info("Engagement statut updated for id={}", id);
        return mapToResponse(engagement);
    }

    private EngagementResponse mapToResponse(Engagement engagement) {
        return EngagementResponse.builder()
                .id(engagement.getId())
                .cotisationId(engagement.getCotisationId())
                .membreId(engagement.getMembreId())
                .montantEngage(engagement.getMontantEngage())
                .montantPaye(engagement.getMontantPaye())
                .periodicite(engagement.getPeriodicite())
                .periodeDebut(engagement.getPeriodeDebut())
                .periodeFin(engagement.getPeriodeFin())
                .statut(engagement.getStatut())
                .tag(engagement.getTag())
                .createdAt(engagement.getCreatedAt())
                .updatedAt(engagement.getUpdatedAt())
                .build();
    }
}
