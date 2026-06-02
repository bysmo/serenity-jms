package com.serenity.nanocredit.service;

import com.serenity.common.dto.ApiResponse;
import com.serenity.common.exception.BusinessException;
import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.common.util.ChecksumUtil;
import com.serenity.nanocredit.client.AccountServiceClient;
import com.serenity.nanocredit.client.MemberServiceClient;
import com.serenity.nanocredit.client.PaymentGatewayServiceClient;
import com.serenity.nanocredit.client.dto.AccountingEntryRequest;
import com.serenity.nanocredit.client.dto.CaisseRequest;
import com.serenity.nanocredit.client.dto.CaisseResponse;
import com.serenity.nanocredit.client.dto.DisbursementRequest;
import com.serenity.nanocredit.client.dto.MembreSummaryResponse;
import com.serenity.nanocredit.client.dto.MouvementCaisseResponse;
import com.serenity.nanocredit.client.dto.PaymentTransactionResponse;
import com.serenity.nanocredit.dto.EligibilityRequest;
import com.serenity.nanocredit.dto.EligibilityResponse;
import com.serenity.nanocredit.dto.EtudeRequest;
import com.serenity.nanocredit.dto.NanoCreditEcheanceResponse;
import com.serenity.nanocredit.dto.NanoCreditGarantResponse;
import com.serenity.nanocredit.dto.NanoCreditResponse;
import com.serenity.nanocredit.dto.NanoCreditVersementResponse;
import com.serenity.nanocredit.dto.RemboursementRequest;
import com.serenity.nanocredit.entity.NanoCredit;
import com.serenity.nanocredit.entity.NanoCreditEcheance;
import com.serenity.nanocredit.entity.NanoCreditGarant;
import com.serenity.nanocredit.entity.NanoCreditPalier;
import com.serenity.nanocredit.entity.NanoCreditVersement;
import com.serenity.nanocredit.entity.enums.EcheanceStatut;
import com.serenity.nanocredit.entity.enums.GarantStatut;
import com.serenity.nanocredit.entity.enums.NanoCreditStatut;
import com.serenity.nanocredit.event.NanoCreditEventPublisher;
import com.serenity.nanocredit.repository.NanoCreditEcheanceRepository;
import com.serenity.nanocredit.repository.NanoCreditGarantRepository;
import com.serenity.nanocredit.repository.NanoCreditPalierRepository;
import com.serenity.nanocredit.repository.NanoCreditRepository;
import com.serenity.nanocredit.repository.NanoCreditVersementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NanoCreditService {

    private final NanoCreditRepository nanoCreditRepository;
    private final NanoCreditPalierRepository palierRepository;
    private final NanoCreditEcheanceRepository echeanceRepository;
    private final NanoCreditVersementRepository versementRepository;
    private final NanoCreditGarantRepository garantRepository;
    private final NanoCreditPalierService palierService;
    private final AmortizationService amortizationService;
    private final NanoCreditEventPublisher eventPublisher;
    private final MemberServiceClient memberServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final PaymentGatewayServiceClient paymentGatewayServiceClient;

    // ==================== CREATE DEMANDE ====================

    @Transactional
    public NanoCreditResponse createDemande(UUID membreId, UUID palierId, BigDecimal montant, String withdrawMode) {
        log.info("Creating nano-credit demande: membreId={}, palierId={}, montant={}", membreId, palierId, montant);

        // Check eligibility
        EligibilityResponse eligibility = palierService.checkEligibility(
                EligibilityRequest.builder().membreId(membreId).palierId(palierId).build());
        if (!eligibility.isEligible()) {
            throw new BusinessException("Membre non éligible: " + String.join(", ", eligibility.getMotifs()), "ELIGIBILITY_FAILED");
        }

        // Verify palier
        NanoCreditPalier palier = palierRepository.findById(palierId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCreditPalier", palierId));

        // Verify montant doesn't exceed palier cap
        if (palier.getMontantPlafond() != null && montant.compareTo(palier.getMontantPlafond()) > 0) {
            throw new BusinessException(
                    String.format("Montant %s dépasse le plafond du palier %s", montant, palier.getMontantPlafond()),
                    "AMOUNT_EXCEEDS_CAP");
        }

        // Verify member exists
        try {
            ApiResponse<MembreSummaryResponse> membreResponse = memberServiceClient.getMemberById(membreId);
            if (membreResponse == null || membreResponse.getData() == null) {
                throw new BusinessException("Membre introuvable", "MEMBER_NOT_FOUND");
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Could not verify member for id={}: {}", membreId, e.getMessage());
            throw new BusinessException("Impossible de vérifier le membre", "MEMBER_SERVICE_UNAVAILABLE");
        }

        // Create the credit
        NanoCredit credit = NanoCredit.builder()
                .membreId(membreId)
                .palier(palier)
                .montant(montant)
                .statut(NanoCreditStatut.DEMANDE_EN_ATTENTE)
                .withdrawMode(withdrawMode)
                .montantPenalite(BigDecimal.ZERO)
                .joursRetard(0)
                .build();

        credit = nanoCreditRepository.save(credit);

        // Compute checksum
        credit.setChecksum(computeChecksum(credit));
        credit = nanoCreditRepository.save(credit);

        // Publish event
        eventPublisher.publishNanoCreditRequested(credit);

        log.info("Nano-credit demande created: id={}, membreId={}, montant={}", credit.getId(), membreId, montant);
        return mapToResponse(credit);
    }

    // ==================== ETUDE ====================

    @Transactional
    public NanoCreditResponse etude(UUID creditId, BigDecimal scoreAi, BigDecimal scoreHumain) {
        log.info("Studying nano-credit: creditId={}, scoreAi={}, scoreHumain={}", creditId, scoreAi, scoreHumain);

        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));

        if (credit.getStatut() != NanoCreditStatut.DEMANDE_EN_ATTENTE) {
            throw new BusinessException(
                    String.format("Le crédit doit être en attente pour être étudié. Statut actuel: %s", credit.getStatut()),
                    "INVALID_STATUS");
        }

        credit.setScoreAi(scoreAi);
        credit.setScoreHumain(scoreHumain);

        // Compute global score: 60% AI + 40% Human
        BigDecimal scoreGlobal = scoreAi.multiply(new BigDecimal("0.6"))
                .add(scoreHumain.multiply(new BigDecimal("0.4")))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        credit.setScoreGlobal(scoreGlobal);

        credit.setStatut(NanoCreditStatut.EN_ETUDE);
        credit.setUpdatedAt(LocalDateTime.now());
        credit.setChecksum(computeChecksum(credit));

        credit = nanoCreditRepository.save(credit);

        log.info("Nano-credit studied: id={}, scoreGlobal={}", credit.getId(), scoreGlobal);
        return mapToResponse(credit);
    }

    // ==================== ACCORDER ====================

    @Transactional
    public NanoCreditResponse accorder(UUID creditId) {
        log.info("Approving nano-credit: creditId={}", creditId);

        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));

        if (credit.getStatut() != NanoCreditStatut.EN_ETUDE) {
            throw new BusinessException(
                    String.format("Le crédit doit être en étude pour être accordé. Statut actuel: %s", credit.getStatut()),
                    "INVALID_STATUS");
        }

        NanoCreditPalier palier = credit.getPalier();

        // Create amortization schedule
        List<NanoCreditEcheance> echeances = amortizationService.generateEcheances(credit);

        // Set end repayment date based on the last echeance
        if (!echeances.isEmpty()) {
            credit.setDateFinRemboursement(echeances.get(echeances.size() - 1).getDateEcheance());
        } else {
            credit.setDateFinRemboursement(LocalDate.now().plusDays(palier.getDureeJours()));
        }

        // Create caisse accounts via account-service
        try {
            // Credit account
            CaisseResponse compteCredit = accountServiceClient.createCaisse(
                    CaisseRequest.builder()
                            .nom("NC-CREDIT-" + credit.getId().toString().substring(0, 8))
                            .type("CREDIT")
                            .membreId(credit.getMembreId())
                            .build()
            ).getData();
            credit.setCompteCreditId(compteCredit.getId());

            // Repayment account
            CaisseResponse compteRemboursement = accountServiceClient.createCaisse(
                    CaisseRequest.builder()
                            .nom("NC-REMB-" + credit.getId().toString().substring(0, 8))
                            .type("COURANT")
                            .membreId(credit.getMembreId())
                            .build()
            ).getData();
            credit.setCompteRemboursementId(compteRemboursement.getId());

            // Impaye account
            CaisseResponse compteImpaye = accountServiceClient.createCaisse(
                    CaisseRequest.builder()
                            .nom("NC-IMP-" + credit.getId().toString().substring(0, 8))
                            .type("IMPAYES")
                            .membreId(credit.getMembreId())
                            .build()
            ).getData();
            credit.setCompteImpayeId(compteImpaye.getId());

        } catch (Exception e) {
            log.error("Failed to create caisse accounts for creditId={}: {}", creditId, e.getMessage(), e);
            throw new BusinessException("Erreur lors de la création des comptes caisse: " + e.getMessage(), "ACCOUNT_SERVICE_ERROR");
        }

        credit.setStatut(NanoCreditStatut.ACCORDE);
        credit.setDateOctroi(LocalDateTime.now());
        credit.setUpdatedAt(LocalDateTime.now());
        credit.setChecksum(computeChecksum(credit));

        credit = nanoCreditRepository.save(credit);

        log.info("Nano-credit approved: id={}, dateFinRemboursement={}", credit.getId(), credit.getDateFinRemboursement());
        return mapToResponse(credit);
    }

    // ==================== DEBOURSER ====================

    @Transactional
    public NanoCreditResponse debourser(UUID creditId) {
        log.info("Disbursing nano-credit: creditId={}", creditId);

        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));

        if (credit.getStatut() != NanoCreditStatut.ACCORDE) {
            throw new BusinessException(
                    String.format("Le crédit doit être accordé pour être débourssé. Statut actuel: %s", credit.getStatut()),
                    "INVALID_STATUS");
        }

        // Get member info for disbursement
        MembreSummaryResponse membre;
        try {
            ApiResponse<MembreSummaryResponse> membreResponse = memberServiceClient.getMemberById(credit.getMembreId());
            membre = membreResponse.getData();
        } catch (Exception e) {
            throw new BusinessException("Impossible de récupérer les informations du membre", "MEMBER_SERVICE_ERROR");
        }

        // Disburse via payment-gateway-service
        try {
            ApiResponse<PaymentTransactionResponse> disburseResponse = paymentGatewayServiceClient.disburse(
                    DisbursementRequest.builder()
                            .telephone(membre.getTelephone())
                            .montant(credit.getMontant())
                            .withdrawMode(credit.getWithdrawMode())
                            .gateway("PAYDUNYA")
                            .internalReference(credit.getId().toString())
                            .build()
            );

            if (disburseResponse == null || disburseResponse.getData() == null) {
                throw new BusinessException("Réponse vide du service de paiement", "PAYMENT_GATEWAY_ERROR");
            }

            log.info("Disbursement initiated: transactionRef={}", disburseResponse.getData().getReference());

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("Failed to disburse nano-credit creditId={}: {}", creditId, e.getMessage(), e);
            throw new BusinessException("Erreur lors du décaissement: " + e.getMessage(), "PAYMENT_GATEWAY_ERROR");
        }

        // Record double-entry accounting: credit caisse SORTIE, member caisse ENTREE
        try {
            // Sortie from system credit caisse
            accountServiceClient.recordMouvement(
                    AccountingEntryRequest.builder()
                            .caisseId(credit.getCompteCreditId())
                            .montant(credit.getMontant())
                            .sens("SORTIE")
                            .type("DEBOURS_NANO_CREDIT")
                            .description("Décaissement nano-crédit " + credit.getId())
                            .referenceType("NANO_CREDIT")
                            .referenceId(credit.getId())
                            .build()
            );

            // Entree into member's account
            accountServiceClient.recordMouvement(
                    AccountingEntryRequest.builder()
                            .caisseId(credit.getCompteRemboursementId())
                            .montant(credit.getMontant())
                            .sens("ENTREE")
                            .type("DEBOURS_NANO_CREDIT")
                            .description("Réception nano-crédit " + credit.getId())
                            .referenceType("NANO_CREDIT")
                            .referenceId(credit.getId())
                            .build()
            );

        } catch (Exception e) {
            log.error("Failed to record accounting entries for creditId={}: {}", creditId, e.getMessage(), e);
            // Continue - disbursement was successful, accounting can be reconciled later
        }

        credit.setStatut(NanoCreditStatut.DEBOURSE);
        credit.setUpdatedAt(LocalDateTime.now());
        credit.setChecksum(computeChecksum(credit));

        credit = nanoCreditRepository.save(credit);

        // Publish disbursed event
        eventPublisher.publishNanoCreditDisbursed(credit);

        log.info("Nano-credit disbursed: id={}, montant={}", credit.getId(), credit.getMontant());
        return mapToResponse(credit);
    }

    // ==================== REMBOURSER ====================

    @Transactional
    public NanoCreditVersementResponse rembourser(UUID creditId, UUID echeanceId, BigDecimal montant, String modePaiement) {
        log.info("Processing repayment: creditId={}, echeanceId={}, montant={}", creditId, echeanceId, montant);

        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));

        if (credit.getStatut() != NanoCreditStatut.EN_REMBOURSEMENT &&
                credit.getStatut() != NanoCreditStatut.EN_RETARD &&
                credit.getStatut() != NanoCreditStatut.DEBOURSE) {
            throw new BusinessException(
                    String.format("Le crédit doit être en remboursement pour effectuer un paiement. Statut actuel: %s", credit.getStatut()),
                    "INVALID_STATUS");
        }

        // Update status to EN_REMBOURSEMENT if DEBOURSE
        if (credit.getStatut() == NanoCreditStatut.DEBOURSE) {
            credit.setStatut(NanoCreditStatut.EN_REMBOURSEMENT);
        }

        // Find the echeance
        NanoCreditEcheance echeance = echeanceRepository.findById(echeanceId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCreditEcheance", echeanceId));

        if (!echeance.getNanoCredit().getId().equals(creditId)) {
            throw new BusinessException("L'échéance ne correspond pas à ce crédit", "ECHEANCE_MISMATCH");
        }

        // Calculate remaining amount for this echeance
        BigDecimal montantRestant = echeance.getMontant().add(echeance.getMontantPenalite()).subtract(echeance.getMontantPaye());
        if (montant.compareTo(montantRestant) > 0) {
            throw new BusinessException(
                    String.format("Le montant du versement (%s) dépasse le montant restant (%s)", montant, montantRestant),
                    "AMOUNT_EXCEEDS_REMAINING");
        }

        // Create versement
        NanoCreditVersement versement = NanoCreditVersement.builder()
                .nanoCredit(credit)
                .echeance(echeance)
                .montant(montant)
                .modePaiement(modePaiement)
                .reference("VERSEMENT-" + UUID.randomUUID().toString().substring(0, 8))
                .build();

        versement = versementRepository.save(versement);

        // Update echeance
        echeance.setMontantPaye(echeance.getMontantPaye().add(montant));
        echeance.setDatePaiement(LocalDateTime.now());

        BigDecimal montantTotalEcheance = echeance.getMontant().add(echeance.getMontantPenalite());
        if (echeance.getMontantPaye().compareTo(montantTotalEcheance) >= 0) {
            echeance.setStatut(EcheanceStatut.PAYEE);
        } else if (echeance.getMontantPaye().compareTo(BigDecimal.ZERO) > 0) {
            echeance.setStatut(EcheanceStatut.PARTIELLEMENT_PAYEE);
        }
        echeance.setUpdatedAt(LocalDateTime.now());
        echeanceRepository.save(echeance);

        // Record accounting mouvement
        try {
            accountServiceClient.recordMouvement(
                    AccountingEntryRequest.builder()
                            .caisseId(credit.getCompteRemboursementId())
                            .montant(montant)
                            .sens("ENTREE")
                            .type("REMBOURSEMENT_NANO_CREDIT")
                            .description("Remboursement nano-crédit échéance " + echeance.getNumeroEcheance())
                            .referenceType("NANO_CREDIT_ECHEANCE")
                            .referenceId(echeance.getId())
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to record repayment accounting entry for creditId={}: {}", creditId, e.getMessage(), e);
            // Continue - payment was recorded, accounting can be reconciled later
        }

        // Check if all echeances are paid
        long echeancesRestantes = echeanceRepository.countByNanoCreditIdAndStatut(creditId, EcheanceStatut.EN_ATTENTE)
                + echeanceRepository.countByNanoCreditIdAndStatut(creditId, EcheanceStatut.EN_RETARD)
                + echeanceRepository.countByNanoCreditIdAndStatut(creditId, EcheanceStatut.PARTIELLEMENT_PAYEE);

        if (echeancesRestantes == 0) {
            credit.setStatut(NanoCreditStatut.REMBOURSE);
            log.info("Nano-credit fully repaid: id={}", creditId);

            // Release guarantors
            List<NanoCreditGarant> garants = garantRepository.findByNanoCreditId(creditId);
            for (NanoCreditGarant garant : garants) {
                if (garant.getStatut() == GarantStatut.ACTIF) {
                    garant.setStatut(GarantStatut.LIBERE);
                    garant.setUpdatedAt(LocalDateTime.now());
                    garantRepository.save(garant);
                }
            }
        }

        credit.setUpdatedAt(LocalDateTime.now());
        credit.setChecksum(computeChecksum(credit));
        nanoCreditRepository.save(credit);

        log.info("Repayment processed: versementId={}, montant={}", versement.getId(), montant);
        return mapVersementToResponse(versement);
    }

    // ==================== ANNULER ====================

    @Transactional
    public NanoCreditResponse annuler(UUID creditId, String motif) {
        log.info("Cancelling nano-credit: creditId={}, motif={}", creditId, motif);

        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));

        if (credit.getStatut() == NanoCreditStatut.DEBOURSE ||
                credit.getStatut() == NanoCreditStatut.EN_REMBOURSEMENT ||
                credit.getStatut() == NanoCreditStatut.REMBOURSE) {
            throw new BusinessException(
                    String.format("Impossible d'annuler un crédit avec le statut: %s", credit.getStatut()),
                    "INVALID_STATUS");
        }

        credit.setStatut(NanoCreditStatut.ANNULE);
        credit.setUpdatedAt(LocalDateTime.now());
        credit.setChecksum(computeChecksum(credit));

        credit = nanoCreditRepository.save(credit);

        log.info("Nano-credit cancelled: id={}", credit.getId());
        return mapToResponse(credit);
    }

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public NanoCreditResponse getById(UUID creditId) {
        NanoCredit credit = nanoCreditRepository.findById(creditId)
                .orElseThrow(() -> new EntityNotFoundException("NanoCredit", creditId));
        return mapToResponse(credit);
    }

    @Transactional(readOnly = true)
    public List<NanoCreditResponse> getAll() {
        return nanoCreditRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditResponse> getByMembreId(UUID membreId) {
        return nanoCreditRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditResponse> getByStatut(NanoCreditStatut statut) {
        return nanoCreditRepository.findByStatut(statut).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditResponse> getByMembreIdAndStatut(UUID membreId, NanoCreditStatut statut) {
        return nanoCreditRepository.findByMembreIdAndStatut(membreId, statut).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditEcheanceResponse> getEcheances(UUID creditId) {
        return echeanceRepository.findByNanoCreditIdOrderByNumeroEcheance(creditId).stream()
                .map(this::mapEcheanceToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditVersementResponse> getVersements(UUID creditId) {
        return versementRepository.findByNanoCreditIdOrderByDateVersementDesc(creditId).stream()
                .map(this::mapVersementToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NanoCreditGarantResponse> getGarants(UUID creditId) {
        return garantRepository.findByNanoCreditId(creditId).stream()
                .map(this::mapGarantToResponse)
                .toList();
    }

    // ==================== MAPPERS ====================

    private NanoCreditResponse mapToResponse(NanoCredit credit) {
        return NanoCreditResponse.builder()
                .id(credit.getId())
                .membreId(credit.getMembreId())
                .palierId(credit.getPalier() != null ? credit.getPalier().getId() : null)
                .palierNom(credit.getPalier() != null ? credit.getPalier().getNom() : null)
                .montant(credit.getMontant())
                .statut(credit.getStatut() != null ? credit.getStatut().name() : null)
                .withdrawMode(credit.getWithdrawMode())
                .scoreAi(credit.getScoreAi())
                .scoreHumain(credit.getScoreHumain())
                .scoreGlobal(credit.getScoreGlobal())
                .compteRemboursementId(credit.getCompteRemboursementId())
                .compteCreditId(credit.getCompteCreditId())
                .compteImpayeId(credit.getCompteImpayeId())
                .dateOctroi(credit.getDateOctroi())
                .dateFinRemboursement(credit.getDateFinRemboursement())
                .montantPenalite(credit.getMontantPenalite())
                .joursRetard(credit.getJoursRetard())
                .dateDernierCalculPenalite(credit.getDateDernierCalculPenalite())
                .createdBy(credit.getCreatedBy())
                .checksum(credit.getChecksum())
                .createdAt(credit.getCreatedAt())
                .updatedAt(credit.getUpdatedAt())
                .build();
    }

    private NanoCreditEcheanceResponse mapEcheanceToResponse(NanoCreditEcheance echeance) {
        return NanoCreditEcheanceResponse.builder()
                .id(echeance.getId())
                .nanoCreditId(echeance.getNanoCredit().getId())
                .numeroEcheance(echeance.getNumeroEcheance())
                .montant(echeance.getMontant())
                .montantPenalite(echeance.getMontantPenalite())
                .dateEcheance(echeance.getDateEcheance())
                .statut(echeance.getStatut() != null ? echeance.getStatut().name() : null)
                .datePaiement(echeance.getDatePaiement())
                .montantPaye(echeance.getMontantPaye())
                .createdAt(echeance.getCreatedAt())
                .updatedAt(echeance.getUpdatedAt())
                .build();
    }

    private NanoCreditVersementResponse mapVersementToResponse(NanoCreditVersement versement) {
        return NanoCreditVersementResponse.builder()
                .id(versement.getId())
                .nanoCreditId(versement.getNanoCredit().getId())
                .echeanceId(versement.getEcheance() != null ? versement.getEcheance().getId() : null)
                .montant(versement.getMontant())
                .dateVersement(versement.getDateVersement())
                .modePaiement(versement.getModePaiement())
                .reference(versement.getReference())
                .createdAt(versement.getCreatedAt())
                .build();
    }

    private NanoCreditGarantResponse mapGarantToResponse(NanoCreditGarant garant) {
        // Try to get garant member info
        String garantNom = null;
        String garantPrenom = null;
        try {
            ApiResponse<MembreSummaryResponse> response = memberServiceClient.getMemberById(garant.getGarantMembreId());
            if (response != null && response.getData() != null) {
                garantNom = response.getData().getNom();
                garantPrenom = response.getData().getPrenom();
            }
        } catch (Exception e) {
            log.debug("Could not fetch garant member info for id={}", garant.getGarantMembreId());
        }

        return NanoCreditGarantResponse.builder()
                .id(garant.getId())
                .nanoCreditId(garant.getNanoCredit().getId())
                .garantMembreId(garant.getGarantMembreId())
                .garantNom(garantNom)
                .garantPrenom(garantPrenom)
                .qualite(garant.getQualite())
                .soldeGarantie(garant.getSoldeGarantie())
                .pourcentagePartage(garant.getPourcentagePartage())
                .statut(garant.getStatut() != null ? garant.getStatut().name() : null)
                .createdAt(garant.getCreatedAt())
                .updatedAt(garant.getUpdatedAt())
                .build();
    }

    private String computeChecksum(NanoCredit credit) {
        String data = String.format("%s|%s|%s|%s|%s|%s",
                credit.getId(),
                credit.getMembreId(),
                credit.getMontant(),
                credit.getStatut(),
                credit.getPalier() != null ? credit.getPalier().getId() : "",
                credit.getUpdatedAt());
        return ChecksumUtil.sha256(data);
    }
}
