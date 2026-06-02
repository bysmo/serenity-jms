package com.serenity.epargne.service;

import com.serenity.epargne.client.AccountServiceClient;
import com.serenity.epargne.client.MemberServiceClient;
import com.serenity.epargne.client.dto.AccountingEntryRequest;
import com.serenity.epargne.client.dto.ApiResponse;
import com.serenity.epargne.client.dto.CaisseRequest;
import com.serenity.epargne.client.dto.CaisseResponse;
import com.serenity.epargne.client.dto.MembreSummaryResponse;
import com.serenity.epargne.entity.EpargnePlan;
import com.serenity.epargne.entity.EpargneSouscription;
import com.serenity.epargne.enums.EpargneFrequence;
import com.serenity.epargne.enums.SouscriptionStatut;
import com.serenity.epargne.event.EpargneKafkaPublisher;
import com.serenity.epargne.repository.EpargneSouscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpargneSouscriptionService {

    private final EpargneSouscriptionRepository souscriptionRepository;
    private final EpargnePlanService planService;
    private final EpargneEcheanceService echeanceService;
    private final AccountServiceClient accountServiceClient;
    private final MemberServiceClient memberServiceClient;
    private final EpargneKafkaPublisher kafkaPublisher;

    @Transactional
    public EpargneSouscription souscrire(UUID membreId, UUID planId, BigDecimal montant) {
        log.info("Member {} subscribing to plan {} with amount {}", membreId, planId, montant);

        // 1. Verify member exists
        verifyMemberExists(membreId);

        // 2. Verify plan is active
        EpargnePlan plan = planService.getById(planId);
        if (!Boolean.TRUE.equals(plan.getActif())) {
            throw new RuntimeException("Le plan d'épargne n'est pas actif: " + planId);
        }

        // 3. Verify montant is within plan limits
        verifyMontantWithinLimits(plan, montant);

        // 4. Create souscription
        EpargneSouscription souscription = new EpargneSouscription();
        souscription.setMembreId(membreId);
        souscription.setPlan(plan);
        souscription.setMontant(montant);
        souscription.setStatut(SouscriptionStatut.ACTIVE);
        souscription.setDateSouscription(java.time.LocalDateTime.now());

        // Calculate date_fin based on plan duree
        if (plan.getDureeMois() != null) {
            souscription.setDateFin(LocalDate.now().plusMonths(plan.getDureeMois()));
        }

        souscription = souscriptionRepository.save(souscription);

        // 5. Generate echeances
        echeanceService.generateEcheances(souscription);

        // 6. Create tontine caisse via account-service
        UUID caisseId = createTontineCaisse(plan, souscription);
        souscription.setCaisseId(caisseId);
        souscription = souscriptionRepository.save(souscription);

        // 7. Record initial entry in account-service
        recordInitialEntry(souscription);

        // 8. Publish EpargneSubscribedEvent
        kafkaPublisher.publishEpargneSubscribed(souscription);

        log.info("Souscription created successfully: {}", souscription.getId());
        return souscription;
    }

    @Transactional(readOnly = true)
    public List<EpargneSouscription> getByMembre(UUID membreId) {
        log.debug("Fetching souscriptions for member: {}", membreId);
        return souscriptionRepository.findByMembreId(membreId);
    }

    @Transactional(readOnly = true)
    public List<EpargneSouscription> getByPlan(UUID planId) {
        log.debug("Fetching souscriptions for plan: {}", planId);
        return souscriptionRepository.findByPlanId(planId);
    }

    @Transactional(readOnly = true)
    public EpargneSouscription getById(UUID id) {
        log.debug("Fetching souscription by id: {}", id);
        return souscriptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Souscription non trouvée avec l'id: " + id));
    }

    @Transactional
    public EpargneSouscription annuler(UUID souscriptionId) {
        log.info("Cancelling souscription: {}", souscriptionId);

        EpargneSouscription souscription = getById(souscriptionId);

        if (souscription.getStatut() == SouscriptionStatut.ANNULEE) {
            throw new RuntimeException("La souscription est déjà annulée: " + souscriptionId);
        }
        if (souscription.getStatut() == SouscriptionStatut.TERMINEE) {
            throw new RuntimeException("Impossible d'annuler une souscription terminée: " + souscriptionId);
        }

        souscription.setStatut(SouscriptionStatut.ANNULEE);
        return souscriptionRepository.save(souscription);
    }

    private void verifyMemberExists(UUID membreId) {
        try {
            ApiResponse<MembreSummaryResponse> response = memberServiceClient.getMemberById(membreId);
            if (response == null || !response.isSuccess() || response.getData() == null) {
                throw new RuntimeException("Membre non trouvé avec l'id: " + membreId);
            }
            MembreSummaryResponse member = response.getData();
            if (!Boolean.TRUE.equals(member.getActif())) {
                throw new RuntimeException("Le membre n'est pas actif: " + membreId);
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error verifying member {}: {}", membreId, e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification du membre: " + e.getMessage());
        }
    }

    private void verifyMontantWithinLimits(EpargnePlan plan, BigDecimal montant) {
        if (plan.getMontantMin() != null && montant.compareTo(plan.getMontantMin()) < 0) {
            throw new RuntimeException(
                    String.format("Le montant %s est inférieur au montant minimum %s du plan",
                            montant, plan.getMontantMin()));
        }
        if (plan.getMontantMax() != null && montant.compareTo(plan.getMontantMax()) > 0) {
            throw new RuntimeException(
                    String.format("Le montant %s est supérieur au montant maximum %s du plan",
                            montant, plan.getMontantMax()));
        }
    }

    private UUID createTontineCaisse(EpargnePlan plan, EpargneSouscription souscription) {
        try {
            String caisseNom = String.format("Caisse Tontine - %s - Membre %s",
                    plan.getNom(), souscription.getMembreId());

            CaisseRequest caisseRequest = new CaisseRequest(
                    caisseNom,
                    "Caisse tontine pour souscription épargne",
                    "TONTINE",
                    true
            );

            ApiResponse<CaisseResponse> response = accountServiceClient.createCaisse(caisseRequest);
            if (response != null && response.isSuccess() && response.getData() != null) {
                log.info("Tontine caisse created: {}", response.getData().getId());
                return response.getData().getId();
            } else {
                log.warn("Failed to create tontine caisse, proceeding without caisse");
                return null;
            }
        } catch (Exception e) {
            log.error("Error creating tontine caisse: {}", e.getMessage());
            return null;
        }
    }

    private void recordInitialEntry(EpargneSouscription souscription) {
        if (souscription.getCaisseId() == null) {
            log.warn("No caisse ID for souscription {}, skipping initial entry", souscription.getId());
            return;
        }

        try {
            AccountingEntryRequest entryRequest = new AccountingEntryRequest(
                    souscription.getCaisseId(),
                    "ENTREE",
                    souscription.getMontant(),
                    String.format("Souscription épargne - Plan %s",
                            souscription.getPlan() != null ? souscription.getPlan().getNom() : "N/A"),
                    souscription.getId().toString(),
                    souscription.getId(),
                    "SOUSCRIPTION_EPARGNE"
            );

            accountServiceClient.recordMouvement(entryRequest);
            log.info("Initial entry recorded for souscription: {}", souscription.getId());
        } catch (Exception e) {
            log.error("Error recording initial entry for souscription {}: {}",
                    souscription.getId(), e.getMessage());
        }
    }
}
