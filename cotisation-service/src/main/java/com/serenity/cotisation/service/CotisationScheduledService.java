package com.serenity.cotisation.service;

import com.serenity.cotisation.entity.Engagement;
import com.serenity.cotisation.enums.EngagementStatut;
import com.serenity.cotisation.event.CotisationEventPublisher;
import com.serenity.cotisation.repository.EngagementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotisationScheduledService {

    private final EngagementRepository engagementRepository;
    private final CotisationEventPublisher eventPublisher;

    /**
     * Check overdue payments - daily at 8h
     * Find engagements with statut EN_COURS where periode_fin < today
     * Update statut to EN_RETARD
     * Publish PaymentOverdueEvent for each
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void checkOverduePayments() {
        log.info("=== Scheduled Task: Checking overdue payments ===");
        LocalDate today = LocalDate.now();
        List<Engagement> overdueEngagements = engagementRepository
                .findByStatutAndPeriodeFinBefore(EngagementStatut.EN_COURS, today);

        if (overdueEngagements.isEmpty()) {
            log.info("No overdue payments found");
            return;
        }

        log.info("Found {} overdue engagement(s)", overdueEngagements.size());
        for (Engagement engagement : overdueEngagements) {
            log.info("Marking engagement id={} as EN_RETARD (periode_fin={}, membreId={})",
                    engagement.getId(), engagement.getPeriodeFin(), engagement.getMembreId());
            engagement.setStatut(EngagementStatut.EN_RETARD);
            engagementRepository.save(engagement);

            // Publish PaymentOverdueEvent
            eventPublisher.publishPaymentOverdue(engagement);
        }
        log.info("=== Overdue payments check completed: {} engagement(s) marked as EN_RETARD ===", overdueEngagements.size());
    }

    /**
     * Check upcoming payments - daily at 7h
     * Find engagements expiring in next 7 days
     * Log warnings (notification-service will handle alerts)
     */
    @Scheduled(cron = "0 0 7 * * ?")
    @Transactional(readOnly = true)
    public void checkUpcomingPayments() {
        log.info("=== Scheduled Task: Checking upcoming payments ===");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);

        List<Engagement> upcomingEngagements = engagementRepository
                .findByStatutAndPeriodeFinBetween(EngagementStatut.EN_COURS, today, sevenDaysFromNow);

        if (upcomingEngagements.isEmpty()) {
            log.info("No upcoming payments expiring in the next 7 days");
            return;
        }

        log.warn("Found {} engagement(s) expiring in the next 7 days", upcomingEngagements.size());
        for (Engagement engagement : upcomingEngagements) {
            log.warn("Engagement id={} for membreId={} expires on {} (cotisationId={})",
                    engagement.getId(),
                    engagement.getMembreId(),
                    engagement.getPeriodeFin(),
                    engagement.getCotisationId());
        }
        log.info("=== Upcoming payments check completed: {} engagement(s) expiring soon ===", upcomingEngagements.size());
    }

    /**
     * Check upcoming engagements - daily at 7h30
     * Find engagements starting soon
     */
    @Scheduled(cron = "0 30 7 * * ?")
    @Transactional(readOnly = true)
    public void checkUpcomingEngagements() {
        log.info("=== Scheduled Task: Checking upcoming engagements ===");
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysFromNow = today.plusDays(7);

        List<Engagement> startingSoon = engagementRepository
                .findByStatutAndPeriodeDebutBetween(EngagementStatut.EN_COURS, today, sevenDaysFromNow);

        if (startingSoon.isEmpty()) {
            log.info("No engagements starting in the next 7 days");
            return;
        }

        log.info("Found {} engagement(s) starting in the next 7 days", startingSoon.size());
        for (Engagement engagement : startingSoon) {
            log.info("Engagement id={} for membreId={} starts on {} (cotisationId={})",
                    engagement.getId(),
                    engagement.getMembreId(),
                    engagement.getPeriodeDebut(),
                    engagement.getCotisationId());
        }
        log.info("=== Upcoming engagements check completed: {} engagement(s) starting soon ===", startingSoon.size());
    }
}
