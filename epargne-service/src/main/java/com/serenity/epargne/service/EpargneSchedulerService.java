package com.serenity.epargne.service;

import com.serenity.epargne.entity.EpargneEcheance;
import com.serenity.epargne.entity.EpargneSouscription;
import com.serenity.epargne.enums.EcheanceStatut;
import com.serenity.epargne.enums.SouscriptionStatut;
import com.serenity.epargne.event.EpargneKafkaPublisher;
import com.serenity.epargne.repository.EpargneEcheanceRepository;
import com.serenity.epargne.repository.EpargneSouscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpargneSchedulerService {

    private final EpargneEcheanceRepository echeanceRepository;
    private final EpargneSouscriptionRepository souscriptionRepository;
    private final EpargneKafkaPublisher kafkaPublisher;

    /**
     * Send tontine reminders daily at 8h00.
     * Finds echeances due in the next 24-48 hours and publishes reminder events.
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void sendTontineReminders() {
        log.info("Starting scheduled task: sendTontineReminders at {}", LocalDateTime.now());

        LocalDate today = LocalDate.now();
        LocalDate startRange = today.plusDays(1);   // Tomorrow
        LocalDate endRange = today.plusDays(2);      // Day after tomorrow

        List<EpargneEcheance> upcomingEcheances = echeanceRepository.findEcheancesDueBetween(
                startRange,
                endRange,
                List.of(EcheanceStatut.EN_ATTENTE, EcheanceStatut.PARTIELLEMENT_PAYEE)
        );

        log.info("Found {} echeances due in next 24-48 hours", upcomingEcheances.size());

        for (EpargneEcheance echeance : upcomingEcheances) {
            try {
                // Get the souscription to find the membreId
                EpargneSouscription souscription = echeance.getSouscription();
                if (souscription == null) {
                    // Fallback: fetch from repository if not loaded
                    souscription = souscriptionRepository.findById(echeance.getSouscriptionId())
                            .orElse(null);
                }

                if (souscription != null && souscription.getStatut() == SouscriptionStatut.ACTIVE) {
                    UUID membreId = souscription.getMembreId();
                    kafkaPublisher.publishEpargneReminder(echeance, membreId);
                    log.debug("Reminder sent for echeance {} to membre {}",
                            echeance.getId(), membreId);
                }
            } catch (Exception e) {
                log.error("Error sending reminder for echeance {}: {}",
                        echeance.getId(), e.getMessage());
            }
        }

        log.info("Completed scheduled task: sendTontineReminders. Processed {} reminders",
                upcomingEcheances.size());
    }

    /**
     * Update echeance statuts daily at 00h30.
     * Marks overdue echeances as EN_RETARD and updates souscription statuts accordingly.
     */
    @Scheduled(cron = "0 30 0 * * ?")
    @Transactional
    public void updateEcheancesStatuts() {
        log.info("Starting scheduled task: updateEcheancesStatuts at {}", LocalDateTime.now());

        LocalDate today = LocalDate.now();

        // 1. Find all echeances that are EN_ATTENTE or PARTIELLEMENT_PAYEE with past due dates
        List<EpargneEcheance> overdueEcheances = echeanceRepository.findOverdueEcheances(
                EcheanceStatut.EN_ATTENTE, today);

        // Also find partially paid overdue echeances
        List<EpargneEcheance> partiallyPaidOverdue = echeanceRepository.findByStatutAndDateEcheanceBefore(
                EcheanceStatut.PARTIELLEMENT_PAYEE, today);

        overdueEcheances.addAll(partiallyPaidOverdue);

        log.info("Found {} overdue echeances to update", overdueEcheances.size());

        // Track which souscriptions need status update
        Set<UUID> affectedSouscriptionIds = new HashSet<>();

        // 2. Mark overdue echeances as EN_RETARD
        for (EpargneEcheance echeance : overdueEcheances) {
            echeance.setStatut(EcheanceStatut.EN_RETARD);
            echeanceRepository.save(echeance);
            affectedSouscriptionIds.add(echeance.getSouscriptionId());
            log.debug("Echeance {} marked as EN_RETARD (due date: {})",
                    echeance.getId(), echeance.getDateEcheance());
        }

        // 3. Update souscription statuts - if any echeance is overdue, mark souscription as EN_RETARD
        for (UUID souscriptionId : affectedSouscriptionIds) {
            try {
                EpargneSouscription souscription = souscriptionRepository.findById(souscriptionId)
                        .orElse(null);

                if (souscription != null && souscription.getStatut() == SouscriptionStatut.ACTIVE) {
                    // Check if any echeance is in EN_RETARD for this souscription
                    List<EpargneEcheance> retardEcheances = echeanceRepository
                            .findBySouscriptionIdAndStatut(souscriptionId, EcheanceStatut.EN_RETARD);

                    if (!retardEcheances.isEmpty()) {
                        souscription.setStatut(SouscriptionStatut.EN_RETARD);
                        souscriptionRepository.save(souscription);
                        log.info("Souscription {} marked as EN_RETARD due to {} overdue echeances",
                                souscriptionId, retardEcheances.size());
                    }
                }
            } catch (Exception e) {
                log.error("Error updating souscription {} statut: {}",
                        souscriptionId, e.getMessage());
            }
        }

        // 4. Check for souscriptions that should be TERMINEE
        // A souscription is TERMINEE if all echeances are PAYEE
        checkCompletedSouscriptions();

        log.info("Completed scheduled task: updateEcheancesStatuts. Updated {} echeances, {} souscriptions affected",
                overdueEcheances.size(), affectedSouscriptionIds.size());
    }

    private void checkCompletedSouscriptions() {
        List<EpargneSouscription> activeAndRetardSouscriptions = souscriptionRepository
                .findByStatut(SouscriptionStatut.ACTIVE);
        activeAndRetardSouscriptions.addAll(
                souscriptionRepository.findByStatut(SouscriptionStatut.EN_RETARD));

        for (EpargneSouscription souscription : activeAndRetardSouscriptions) {
            try {
                List<EpargneEcheance> allEcheances = echeanceRepository
                        .findBySouscriptionIdOrderByNumeroEcheanceAsc(souscription.getId());

                if (allEcheances.isEmpty()) {
                    continue;
                }

                boolean allPayee = allEcheances.stream()
                        .allMatch(e -> e.getStatut() == EcheanceStatut.PAYEE);

                if (allPayee) {
                    souscription.setStatut(SouscriptionStatut.TERMINEE);
                    souscriptionRepository.save(souscription);
                    log.info("Souscription {} marked as TERMINEE - all echeances paid",
                            souscription.getId());
                }
            } catch (Exception e) {
                log.error("Error checking completed souscription {}: {}",
                        souscription.getId(), e.getMessage());
            }
        }
    }
}
