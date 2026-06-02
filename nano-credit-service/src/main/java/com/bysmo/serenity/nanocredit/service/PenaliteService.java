package com.bysmo.serenity.nanocredit.service;

import com.bysmo.serenity.nanocredit.entity.NanoCredit;
import com.bysmo.serenity.nanocredit.entity.NanoCreditEcheance;
import com.bysmo.serenity.nanocredit.entity.NanoCreditPalier;
import com.bysmo.serenity.nanocredit.entity.enums.EcheanceStatut;
import com.bysmo.serenity.nanocredit.entity.enums.NanoCreditStatut;
import com.bysmo.serenity.nanocredit.event.NanoCreditEventPublisher;
import com.bysmo.serenity.nanocredit.repository.NanoCreditEcheanceRepository;
import com.bysmo.serenity.nanocredit.repository.NanoCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PenaliteService {

    private final NanoCreditRepository nanoCreditRepository;
    private final NanoCreditEcheanceRepository echeanceRepository;
    private final NanoCreditEventPublisher eventPublisher;

    /**
     * Calculates penalties for overdue echeances of a specific credit.
     * Penalty = penalite_par_jour * number_of_days_late for each overdue echeance.
     *
     * @param credit the nano-credit
     * @return total penalty amount
     */
    public BigDecimal calculerPenalites(NanoCredit credit) {
        NanoCreditPalier palier = credit.getPalier();
        BigDecimal penaliteParJour = palier.getPenaliteParJour();

        if (penaliteParJour == null || penaliteParJour.compareTo(BigDecimal.ZERO) <= 0) {
            log.debug("No penalty rate configured for palier {}", palier.getNumero());
            return BigDecimal.ZERO;
        }

        LocalDate today = LocalDate.now();
        List<NanoCreditEcheance> echeancesEnRetard = echeanceRepository
                .findByNanoCreditIdAndStatut(credit.getId(), EcheanceStatut.EN_RETARD);

        BigDecimal totalPenalite = BigDecimal.ZERO;

        for (NanoCreditEcheance echeance : echeancesEnRetard) {
            long joursRetard = ChronoUnit.DAYS.between(echeance.getDateEcheance(), today);
            if (joursRetard > 0) {
                // Calculate penalty: only for days not yet penalized
                LocalDateTime dernierCalcul = credit.getDateDernierCalculPenalite();
                long joursDejaPenalises = 0;
                if (dernierCalcul != null) {
                    joursDejaPenalises = ChronoUnit.DAYS.between(dernierCalcul.toLocalDate(), today);
                    if (joursDejaPenalises < 0) joursDejaPenalises = 0;
                } else {
                    joursDejaPenalises = joursRetard;
                }

                BigDecimal penaliteEcheance = penaliteParJour.multiply(BigDecimal.valueOf(joursDejaPenalises));
                totalPenalite = totalPenalite.add(penaliteEcheance);

                log.debug("Echeance {} jours_retard={}, penalite={}", echeance.getNumeroEcheance(), joursDejaPenalises, penaliteEcheance);
            }
        }

        return totalPenalite.setScale(4, java.math.RoundingMode.HALF_UP);
    }

    /**
     * Applies penalties to all credits that are in EN_REMBOURSEMENT or EN_RETARD status.
     * This method is called by the scheduled task.
     */
    @Transactional
    public void appliquerPenalites() {
        log.info("Starting penalty calculation for all active nano-credits");

        List<NanoCreditStatut> statutsActifs = List.of(
                NanoCreditStatut.EN_REMBOURSEMENT,
                NanoCreditStatut.EN_RETARD
        );

        List<NanoCredit> creditsActifs = nanoCreditRepository.findByStatutIn(statutsActifs);

        int penalitesAppliquees = 0;

        for (NanoCredit credit : creditsActifs) {
            try {
                BigDecimal penalite = calculerPenalites(credit);

                if (penalite.compareTo(BigDecimal.ZERO) > 0) {
                    credit.setMontantPenalite(credit.getMontantPenalite().add(penalite));

                    // Update jours_retard
                    List<NanoCreditEcheance> echeancesEnRetard = echeanceRepository
                            .findByNanoCreditIdAndStatut(credit.getId(), EcheanceStatut.EN_RETARD);
                    int maxJoursRetard = echeancesEnRetard.stream()
                            .mapToInt(e -> (int) ChronoUnit.DAYS.between(e.getDateEcheance(), LocalDate.now()))
                            .max()
                            .orElse(0);
                    credit.setJoursRetard(maxJoursRetard);

                    credit.setDateDernierCalculPenalite(LocalDateTime.now());
                    credit.setUpdatedAt(LocalDateTime.now());

                    // Update credit status to EN_RETARD if not already
                    if (credit.getStatut() == NanoCreditStatut.EN_REMBOURSEMENT && maxJoursRetard > 0) {
                        credit.setStatut(NanoCreditStatut.EN_RETARD);
                    }

                    // Apply penalty to individual echeances
                    for (NanoCreditEcheance echeance : echeancesEnRetard) {
                        long joursRetard = ChronoUnit.DAYS.between(echeance.getDateEcheance(), LocalDate.now());
                        BigDecimal penaliteEcheance = credit.getPalier().getPenaliteParJour()
                                .multiply(BigDecimal.valueOf(joursRetard));
                        echeance.setMontantPenalite(penaliteEcheance);
                        echeance.setUpdatedAt(LocalDateTime.now());
                        echeanceRepository.save(echeance);
                    }

                    nanoCreditRepository.save(credit);

                    // Publish penalty event
                    eventPublisher.publishNanoCreditPenaltyApplied(credit, penalite, maxJoursRetard);

                    penalitesAppliquees++;
                    log.info("Penalty applied: creditId={}, montantPenalite={}, joursRetard={}",
                            credit.getId(), penalite, maxJoursRetard);
                }
            } catch (Exception e) {
                log.error("Error applying penalty for creditId={}: {}", credit.getId(), e.getMessage(), e);
            }
        }

        log.info("Penalty calculation completed. {} credits had penalties applied.", penalitesAppliquees);
    }

    /**
     * Updates echeance statuses: marks overdue echeances as EN_RETARD.
     */
    @Transactional
    public void updateEcheancesStatuts() {
        log.info("Updating echeance statuses for overdue echeances");

        LocalDate today = LocalDate.now();
        List<NanoCreditEcheance> echeancesEnAttente = echeanceRepository
                .findByStatutAndDateEcheanceBefore(EcheanceStatut.EN_ATTENTE, today);

        int updated = 0;
        for (NanoCreditEcheance echeance : echeancesEnAttente) {
            echeance.setStatut(EcheanceStatut.EN_RETARD);
            echeance.setUpdatedAt(LocalDateTime.now());
            echeanceRepository.save(echeance);
            updated++;
        }

        // Also update partially paid echeances that are overdue
        List<NanoCreditEcheance> partiellementPayees = echeanceRepository
                .findByStatutAndDateEcheanceBefore(EcheanceStatut.PARTIELLEMENT_PAYEE, today);
        for (NanoCreditEcheance echeance : partiellementPayees) {
            echeance.setStatut(EcheanceStatut.EN_RETARD);
            echeance.setUpdatedAt(LocalDateTime.now());
            echeanceRepository.save(echeance);
            updated++;
        }

        // Update credit statuses based on echeance statuses
        List<NanoCreditStatut> statutsActifs = List.of(
                NanoCreditStatut.EN_REMBOURSEMENT,
                NanoCreditStatut.EN_RETARD
        );
        List<NanoCredit> creditsActifs = nanoCreditRepository.findByStatutIn(statutsActifs);

        for (NanoCredit credit : creditsActifs) {
            long echeancesEnRetard = echeanceRepository.countByNanoCreditIdAndStatut(
                    credit.getId(), EcheanceStatut.EN_RETARD);

            if (echeancesEnRetard > 0 && credit.getStatut() != NanoCreditStatut.EN_RETARD) {
                credit.setStatut(NanoCreditStatut.EN_RETARD);
                credit.setUpdatedAt(LocalDateTime.now());
                nanoCreditRepository.save(credit);
            } else if (echeancesEnRetard == 0 && credit.getStatut() == NanoCreditStatut.EN_RETARD) {
                credit.setStatut(NanoCreditStatut.EN_REMBOURSEMENT);
                credit.setUpdatedAt(LocalDateTime.now());
                nanoCreditRepository.save(credit);
            }
        }

        log.info("Echeance status update completed. {} echeances updated.", updated);
    }
}
