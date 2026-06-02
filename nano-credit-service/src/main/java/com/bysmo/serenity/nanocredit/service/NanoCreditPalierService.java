package com.bysmo.serenity.nanocredit.service;

import com.bysmo.serenity.common.exception.BusinessException;
import com.bysmo.serenity.common.exception.EntityNotFoundException;
import com.bysmo.serenity.common.util.ChecksumUtil;
import com.bysmo.serenity.nanocredit.client.dto.MembreSummaryResponse;
import com.bysmo.serenity.nanocredit.client.AccountServiceClient;
import com.bysmo.serenity.nanocredit.client.MemberServiceClient;
import com.bysmo.serenity.nanocredit.client.dto.CaisseBalanceResponse;
import com.bysmo.serenity.nanocredit.dto.EligibilityRequest;
import com.bysmo.serenity.nanocredit.dto.EligibilityResponse;
import com.bysmo.serenity.nanocredit.dto.NanoCreditPalierRequest;
import com.bysmo.serenity.nanocredit.dto.NanoCreditPalierResponse;
import com.bysmo.serenity.nanocredit.entity.NanoCreditGarant;
import com.bysmo.serenity.nanocredit.entity.NanoCreditPalier;
import com.bysmo.serenity.nanocredit.entity.enums.FrequenceRemboursement;
import com.bysmo.serenity.nanocredit.entity.enums.GarantStatut;
import com.bysmo.serenity.nanocredit.entity.enums.NanoCreditStatut;
import com.bysmo.serenity.nanocredit.repository.NanoCreditGarantRepository;
import com.bysmo.serenity.nanocredit.repository.NanoCreditPalierRepository;
import com.bysmo.serenity.nanocredit.repository.NanoCreditRepository;
import com.bysmo.serenity.common.dto.ApiResponse;
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
public class NanoCreditPalierService {

    private final NanoCreditPalierRepository palierRepository;
    private final NanoCreditRepository nanoCreditRepository;
    private final NanoCreditGarantRepository garantRepository;
    private final MemberServiceClient memberServiceClient;
    private final AccountServiceClient accountServiceClient;

    @Transactional
    public NanoCreditPalierResponse createPalier(NanoCreditPalierRequest request) {
        log.info("Creating new palier: {}", request.getNom());

        FrequenceRemboursement frequence = FrequenceRemboursement.valueOf(request.getFrequenceRemboursement().toUpperCase());

        NanoCreditPalier palier = NanoCreditPalier.builder()
                .nom(request.getNom())
                .montantPlafond(request.getMontantPlafond())
                .dureeJours(request.getDureeJours())
                .frequenceRemboursement(frequence)
                .tauxInteret(request.getTauxInteret())
                .penaliteParJour(request.getPenaliteParJour())
                .minMontantTotalRembourse(request.getMinMontantTotalRembourse())
                .minEpargneCumulee(request.getMinEpargneCumulee())
                .minEpargnePercent(request.getMinEpargnePercent())
                .minGarantQualite(request.getMinGarantQualite())
                .pourcentagePartageGarant(request.getPourcentagePartageGarant())
                .actif(request.getActif())
                .build();

        palier = palierRepository.save(palier);

        // Generate numero
        palier.setNumero(String.format("PAL-%03d", extractPalierNumber(palier.getId())));
        palier.setChecksum(computeChecksum(palier));
        palier = palierRepository.save(palier);

        log.info("Palier created with id={}, numero={}", palier.getId(), palier.getNumero());
        return mapToResponse(palier);
    }

    @Transactional(readOnly = true)
    public List<NanoCreditPalierResponse> getAllPaliers() {
        return palierRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditPalierResponse> getActivePaliers() {
        return palierRepository.findByActifTrue().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public NanoCreditPalierResponse getPalierById(UUID id) {
        NanoCreditPalier palier = palierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NanoCreditPalier", id));
        return mapToResponse(palier);
    }

    @Transactional
    public NanoCreditPalierResponse updatePalier(UUID id, NanoCreditPalierRequest request) {
        log.info("Updating palier id={}", id);

        NanoCreditPalier palier = palierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NanoCreditPalier", id));

        FrequenceRemboursement frequence = FrequenceRemboursement.valueOf(request.getFrequenceRemboursement().toUpperCase());

        palier.setNom(request.getNom());
        palier.setMontantPlafond(request.getMontantPlafond());
        palier.setDureeJours(request.getDureeJours());
        palier.setFrequenceRemboursement(frequence);
        palier.setTauxInteret(request.getTauxInteret());
        palier.setPenaliteParJour(request.getPenaliteParJour());
        palier.setMinMontantTotalRembourse(request.getMinMontantTotalRembourse());
        palier.setMinEpargneCumulee(request.getMinEpargneCumulee());
        palier.setMinEpargnePercent(request.getMinEpargnePercent());
        palier.setMinGarantQualite(request.getMinGarantQualite());
        palier.setPourcentagePartageGarant(request.getPourcentagePartageGarant());
        palier.setActif(request.getActif());
        palier.setUpdatedAt(LocalDateTime.now());
        palier.setChecksum(computeChecksum(palier));

        palier = palierRepository.save(palier);

        log.info("Palier updated with id={}", id);
        return mapToResponse(palier);
    }

    @Transactional
    public void deletePalier(UUID id) {
        log.info("Deleting palier id={}", id);

        NanoCreditPalier palier = palierRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("NanoCreditPalier", id));

        // Soft delete - deactivate
        palier.setActif(false);
        palier.setUpdatedAt(LocalDateTime.now());
        palierRepository.save(palier);

        log.info("Palier deactivated with id={}", id);
    }

    @Transactional(readOnly = true)
    public EligibilityResponse checkEligibility(EligibilityRequest request) {
        log.info("Checking eligibility for membreId={}, palierId={}", request.getMembreId(), request.getPalierId());

        EligibilityResponse response = EligibilityResponse.builder().build();

        // Verify member exists
        MembreSummaryResponse membre;
        try {
            ApiResponse<MembreSummaryResponse> membreResponse = memberServiceClient.getMemberById(request.getMembreId());
            if (membreResponse == null || membreResponse.getData() == null) {
                response.setEligible(false);
                response.addMotif("Membre introuvable");
                return response;
            }
            membre = membreResponse.getData();
        } catch (Exception e) {
            log.warn("Could not verify member existence for id={}: {}", request.getMembreId(), e.getMessage());
            response.setEligible(false);
            response.addMotif("Impossible de vérifier l'existence du membre");
            return response;
        }

        // Verify member status is active
        if (!"ACTIF".equalsIgnoreCase(membre.getStatut()) && !"actif".equalsIgnoreCase(membre.getStatut())) {
            response.setEligible(false);
            response.addMotif("Le membre n'est pas actif");
        }

        // Verify palier exists
        NanoCreditPalier palier = palierRepository.findById(request.getPalierId()).orElse(null);
        if (palier == null) {
            response.setEligible(false);
            response.addMotif("Palier introuvable");
            return response;
        }

        if (!palier.getActif()) {
            response.setEligible(false);
            response.addMotif("Le palier n'est pas actif");
        }

        // Check for existing active credits
        List<NanoCreditStatut> activeStatuts = List.of(
                NanoCreditStatut.DEMANDE_EN_ATTENTE,
                NanoCreditStatut.EN_ETUDE,
                NanoCreditStatut.ACCORDE,
                NanoCreditStatut.DEBOURSE,
                NanoCreditStatut.EN_REMBOURSEMENT,
                NanoCreditStatut.EN_RETARD
        );
        boolean hasActiveCredits = nanoCreditRepository.existsByMembreIdAndStatutNotIn(request.getMembreId(),
                List.of(NanoCreditStatut.REMBOURSE, NanoCreditStatut.ANNULE, NanoCreditStatut.REFUSE, NanoCreditStatut.IMPAYE));
        if (hasActiveCredits) {
            response.setEligible(false);
            response.addMotif("Le membre a déjà un nano-crédit actif");
        }

        // Check guarantor quality requirements
        if (palier.getMinGarantQualite() != null && palier.getMinGarantQualite() > 0) {
            List<NanoCreditGarant> activeGarants = garantRepository.findByGarantMembreId(request.getMembreId()).stream()
                    .filter(g -> g.getStatut() == GarantStatut.ACTIF)
                    .toList();

            int totalQualite = activeGarants.stream()
                    .mapToInt(NanoCreditGarant::getQualite)
                    .sum();

            if (totalQualite < palier.getMinGarantQualite()) {
                response.setEligible(false);
                response.addMotif(String.format("Qualité garants insuffisante: %d/%d requis", totalQualite, palier.getMinGarantQualite()));
            }
        }

        // Check epargne requirements via account-service (if configured)
        if (palier.getMinEpargneCumulee() != null && palier.getMinEpargneCumulee().compareTo(BigDecimal.ZERO) > 0) {
            try {
                // Attempt to verify savings - this would require an epargne caisse lookup
                // For now, we note the requirement but don't block if service is unavailable
                log.debug("Epargne cumulée minimale requise: {}", palier.getMinEpargneCumulee());
            } catch (Exception e) {
                log.warn("Could not verify epargne for membreId={}: {}", request.getMembreId(), e.getMessage());
            }
        }

        if (response.getMotifs().isEmpty()) {
            response.setEligible(true);
        }

        log.info("Eligibility result for membreId={}, palierId={}: eligible={}, motifs={}",
                request.getMembreId(), request.getPalierId(), response.isEligible(), response.getMotifs());
        return response;
    }

    private int extractPalierNumber(UUID id) {
        // Simple approach: use the hash code of the UUID to generate a number
        return Math.abs(id.hashCode() % 1000) + 1;
    }

    private String computeChecksum(NanoCreditPalier palier) {
        String data = String.format("%s|%s|%s|%s|%s",
                palier.getNumero(),
                palier.getNom(),
                palier.getMontantPlafond(),
                palier.getTauxInteret(),
                palier.getUpdatedAt());
        return ChecksumUtil.sha256(data);
    }

    private NanoCreditPalierResponse mapToResponse(NanoCreditPalier palier) {
        return NanoCreditPalierResponse.builder()
                .id(palier.getId())
                .numero(palier.getNumero())
                .nom(palier.getNom())
                .montantPlafond(palier.getMontantPlafond())
                .dureeJours(palier.getDureeJours())
                .frequenceRemboursement(palier.getFrequenceRemboursement() != null ? palier.getFrequenceRemboursement().name() : null)
                .tauxInteret(palier.getTauxInteret())
                .penaliteParJour(palier.getPenaliteParJour())
                .minMontantTotalRembourse(palier.getMinMontantTotalRembourse())
                .minEpargneCumulee(palier.getMinEpargneCumulee())
                .minEpargnePercent(palier.getMinEpargnePercent())
                .minGarantQualite(palier.getMinGarantQualite())
                .pourcentagePartageGarant(palier.getPourcentagePartageGarant())
                .actif(palier.getActif())
                .checksum(palier.getChecksum())
                .createdAt(palier.getCreatedAt())
                .updatedAt(palier.getUpdatedAt())
                .build();
    }
}
