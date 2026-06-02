package com.serenity.nanocredit.service;

import com.serenity.common.dto.ApiResponse;
import com.serenity.common.exception.BusinessException;
import com.serenity.nanocredit.client.AccountServiceClient;
import com.serenity.nanocredit.client.MemberServiceClient;
import com.serenity.nanocredit.client.dto.AccountingEntryRequest;
import com.serenity.nanocredit.client.dto.CaisseBalanceResponse;
import com.serenity.nanocredit.client.dto.MembreSummaryResponse;
import com.serenity.nanocredit.entity.NanoCredit;
import com.serenity.nanocredit.entity.NanoCreditEcheance;
import com.serenity.nanocredit.entity.NanoCreditGarant;
import com.serenity.nanocredit.entity.NanoCreditPalier;
import com.serenity.nanocredit.entity.enums.EcheanceStatut;
import com.serenity.nanocredit.entity.enums.GarantStatut;
import com.serenity.nanocredit.entity.enums.NanoCreditStatut;
import com.serenity.nanocredit.repository.NanoCreditEcheanceRepository;
import com.serenity.nanocredit.repository.NanoCreditGarantRepository;
import com.serenity.nanocredit.repository.NanoCreditPalierRepository;
import com.serenity.nanocredit.repository.NanoCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NanoCreditScheduledService {

    private final NanoCreditRepository nanoCreditRepository;
    private final NanoCreditEcheanceRepository echeanceRepository;
    private final NanoCreditGarantRepository garantRepository;
    private final NanoCreditPalierRepository palierRepository;
    private final PenaliteService penaliteService;
    private final AccountServiceClient accountServiceClient;
    private final MemberServiceClient memberServiceClient;

    /**
     * Apply penalties daily at midnight.
     * Calculates and applies penalties for all overdue echeances.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void appliquerPenalitesNanoCredits() {
        log.info("=== Scheduled Task: Applying penalties to nano-credits ===");
        try {
            penaliteService.appliquerPenalites();
            log.info("=== Scheduled Task: Penalty application completed ===");
        } catch (Exception e) {
            log.error("=== Scheduled Task: Error applying penalties ===", e);
        }
    }

    /**
     * Update echeance statuts daily at 00:30.
     * Marks overdue echeances as EN_RETARD and updates credit statuses accordingly.
     */
    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void updateEcheancesStatuts() {
        log.info("=== Scheduled Task: Updating echeance statuses ===");
        try {
            penaliteService.updateEcheancesStatuts();

            // Also check for IMPAYE status: credits with echeances overdue by more than 30 days
            checkImpayeCredits();

            log.info("=== Scheduled Task: Echeance status update completed ===");
        } catch (Exception e) {
            log.error("=== Scheduled Task: Error updating echeance statuses ===", e);
        }
    }

    /**
     * Check palier upgrades daily at 1h.
     * Reviews members who may qualify for higher palier tiers.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void checkNanoCreditPaliers() {
        log.info("=== Scheduled Task: Checking nano-credit palier upgrades ===");
        try {
            List<NanoCreditPalier> activePaliers = palierRepository.findByActifTrue();

            // Find credits that are fully repaid
            List<NanoCredit> creditsRembourses = nanoCreditRepository.findByStatut(NanoCreditStatut.REMBOURSE);

            int upgradesChecked = 0;
            for (NanoCredit credit : creditsRembourses) {
                try {
                    // Check if the member could qualify for a higher palier
                    // This is informational - actual upgrade happens when member applies
                    NanoCreditPalier currentPalier = credit.getPalier();

                    // Find paliers with higher montant_plafond
                    List<NanoCreditPalier> higherPaliers = activePaliers.stream()
                            .filter(p -> p.getMontantPlafond() != null && currentPalier.getMontantPlafond() != null)
                            .filter(p -> p.getMontantPlafond().compareTo(currentPalier.getMontantPlafond()) > 0)
                            .toList();

                    if (!higherPaliers.isEmpty()) {
                        log.info("Member {} may qualify for higher palier after repaying credit {}",
                                credit.getMembreId(), credit.getId());
                        upgradesChecked++;
                    }
                } catch (Exception e) {
                    log.error("Error checking palier upgrade for creditId={}: {}", credit.getId(), e.getMessage());
                }
            }

            log.info("=== Scheduled Task: Palier upgrade check completed. {} members reviewed ===", upgradesChecked);
        } catch (Exception e) {
            log.error("=== Scheduled Task: Error checking palier upgrades ===", e);
        }
    }

    /**
     * Process garant deductions daily at 2h.
     * For credits in IMPAYE status, deduct from guarantors based on their partage percentage.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void prelevementsGarantsNanoCredits() {
        log.info("=== Scheduled Task: Processing guarantor deductions ===");
        try {
            // Find all credits in EN_RETARD or IMPAYE status
            List<NanoCredit> creditsEnRetard = nanoCreditRepository.findByStatutIn(
                    List.of(NanoCreditStatut.EN_RETARD, NanoCreditStatut.IMPAYE));

            int deductionsProcessed = 0;

            for (NanoCredit credit : creditsEnRetard) {
                try {
                    List<NanoCreditGarant> activeGarants = garantRepository.findByNanoCreditIdAndStatut(
                            credit.getId(), GarantStatut.ACTIF);

                    if (activeGarants.isEmpty()) {
                        continue;
                    }

                    // Calculate total unpaid amount
                    List<NanoCreditEcheance> echeancesImpayees = echeanceRepository
                            .findByNanoCreditIdAndStatut(credit.getId(), EcheanceStatut.EN_RETARD);

                    BigDecimal montantImpaye = echeancesImpayees.stream()
                            .map(e -> e.getMontant().add(e.getMontantPenalite()).subtract(e.getMontantPaye()))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    if (montantImpaye.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    NanoCreditPalier palier = credit.getPalier();
                    BigDecimal pourcentagePartage = palier.getPourcentagePartageGarant();

                    if (pourcentagePartage == null || pourcentagePartage.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }

                    // Calculate amount to be covered by guarantors
                    BigDecimal montantGarants = montantImpaye.multiply(pourcentagePartage)
                            .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                    // Distribute among active guarantors
                    BigDecimal montantParGarant = montantGarants.divide(
                            BigDecimal.valueOf(activeGarants.size()), 4, RoundingMode.HALF_UP);

                    for (NanoCreditGarant garant : activeGarants) {
                        try {
                            // Try to deduct from guarantor's account
                            if (garant.getSoldeGarantie().compareTo(montantParGarant) >= 0) {
                                // Deduct from guarantor's guarantee balance
                                garant.setSoldeGarantie(garant.getSoldeGarantie().subtract(montantParGarant));
                                garant.setUpdatedAt(LocalDateTime.now());

                                // Record accounting entry
                                try {
                                    accountServiceClient.recordMouvement(
                                            AccountingEntryRequest.builder()
                                                    .caisseId(credit.getCompteRemboursementId())
                                                    .montant(montantParGarant)
                                                    .sens("ENTREE")
                                                    .type("REMBOURSEMENT_NANO_CREDIT")
                                                    .description("Prélèvement garant pour nano-crédit " + credit.getId())
                                                    .referenceType("GARANT_PRELEVEMENT")
                                                    .referenceId(garant.getId())
                                                    .build()
                                    );
                                } catch (Exception e) {
                                    log.error("Failed to record garant deduction accounting for garantId={}: {}",
                                            garant.getId(), e.getMessage());
                                }

                                garantRepository.save(garant);
                                deductionsProcessed++;

                                log.info("Garant deduction: garantId={}, montant={}", garant.getId(), montantParGarant);
                            } else if (garant.getSoldeGarantie().compareTo(BigDecimal.ZERO) > 0) {
                                // Partial deduction - guarantor doesn't have enough
                                BigDecimal montantDisponible = garant.getSoldeGarantie();
                                garant.setSoldeGarantie(BigDecimal.ZERO);
                                garant.setStatut(GarantStatut.DEFAILLANT);
                                garant.setUpdatedAt(LocalDateTime.now());

                                try {
                                    accountServiceClient.recordMouvement(
                                            AccountingEntryRequest.builder()
                                                    .caisseId(credit.getCompteRemboursementId())
                                                    .montant(montantDisponible)
                                                    .sens("ENTREE")
                                                    .type("REMBOURSEMENT_NANO_CREDIT")
                                                    .description("Prélèvement partiel garant pour nano-crédit " + credit.getId())
                                                    .referenceType("GARANT_PRELEVEMENT_PARTIEL")
                                                    .referenceId(garant.getId())
                                                    .build()
                                    );
                                } catch (Exception e) {
                                    log.error("Failed to record partial garant deduction for garantId={}: {}",
                                            garant.getId(), e.getMessage());
                                }

                                garantRepository.save(garant);
                                deductionsProcessed++;

                                log.info("Partial garant deduction (defaillant): garantId={}, montant={}", garant.getId(), montantDisponible);
                            } else {
                                // Guarantor has no balance - mark as defaillant
                                garant.setStatut(GarantStatut.DEFAILLANT);
                                garant.setUpdatedAt(LocalDateTime.now());
                                garantRepository.save(garant);

                                log.warn("Garant defaillant (no balance): garantId={}", garant.getId());
                            }
                        } catch (Exception e) {
                            log.error("Error processing garant deduction for garantId={}: {}", garant.getId(), e.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing garant deductions for creditId={}: {}", credit.getId(), e.getMessage());
                }
            }

            log.info("=== Scheduled Task: Guarantor deduction processing completed. {} deductions processed ===", deductionsProcessed);
        } catch (Exception e) {
            log.error("=== Scheduled Task: Error processing guarantor deductions ===", e);
        }
    }

    /**
     * Checks for credits that should be moved to IMPAYE status.
     * A credit is considered IMPAYE when it has echeances overdue by more than 30 days.
     */
    private void checkImpayeCredits() {
        List<NanoCredit> creditsEnRetard = nanoCreditRepository.findByStatut(NanoCreditStatut.EN_RETARD);
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        for (NanoCredit credit : creditsEnRetard) {
            List<NanoCreditEcheance> echeancesEnRetard = echeanceRepository
                    .findByNanoCreditIdAndStatut(credit.getId(), EcheanceStatut.EN_RETARD);

            boolean hasSeverelyOverdue = echeancesEnRetard.stream()
                    .anyMatch(e -> e.getDateEcheance().isBefore(thirtyDaysAgo));

            if (hasSeverelyOverdue) {
                credit.setStatut(NanoCreditStatut.IMPAYE);
                credit.setUpdatedAt(LocalDateTime.now());
                nanoCreditRepository.save(credit);

                log.info("Credit moved to IMPAYE status: creditId={}", credit.getId());
            }
        }
    }
}
