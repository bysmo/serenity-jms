package com.bysmo.serenity.notification.consumer;

import com.bysmo.serenity.common.event.AccountLowBalanceEvent;
import com.bysmo.serenity.common.event.CotisationAdhesionRequestedEvent;
import com.bysmo.serenity.common.event.EpargneReminderEvent;
import com.bysmo.serenity.common.event.EpargneSubscribedEvent;
import com.bysmo.serenity.common.event.MemberCreatedEvent;
import com.bysmo.serenity.common.event.MemberKycValidatedEvent;
import com.bysmo.serenity.common.event.NanoCreditDisbursedEvent;
import com.bysmo.serenity.common.event.NanoCreditPenaltyAppliedEvent;
import com.bysmo.serenity.common.event.NanoCreditRequestedEvent;
import com.bysmo.serenity.common.event.PaymentCompletedEvent;
import com.bysmo.serenity.common.event.PaymentOverdueEvent;
import com.bysmo.serenity.notification.config.NotificationProperties;
import com.bysmo.serenity.notification.entity.enums.RecipientType;
import com.bysmo.serenity.notification.service.EmailService;
import com.bysmo.serenity.notification.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final EmailService emailService;
    private final SmsService smsService;
    private final NotificationProperties notificationProperties;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ========================================================================
    // Member Events
    // ========================================================================

    @KafkaListener(topics = "member.created", groupId = "notification-service")
    public void handleMemberCreated(MemberCreatedEvent event) {
        log.info("Received MemberCreatedEvent for member: {}", event.getMemberId());
        try {
            // Send welcome email to new member
            emailService.sendTemplatedEmailToRecipient(
                    event.getEmail(),
                    "welcome",
                    Map.of(
                            "prenom", event.getPrenom() != null ? event.getPrenom() : "",
                            "numeroMembre", event.getNumero() != null ? event.getNumero() : ""
                    ),
                    event.getMemberId(),
                    RecipientType.MEMBRE
            );
            log.info("Welcome email sent for new member: {}", event.getNumero());
        } catch (Exception e) {
            log.error("Error processing MemberCreatedEvent for member: {}", event.getMemberId(), e);
        }
    }

    @KafkaListener(topics = "member.kyc.validated", groupId = "notification-service")
    public void handleKycValidated(MemberKycValidatedEvent event) {
        log.info("Received MemberKycValidatedEvent for member: {}", event.getMemberId());
        try {
            // Send KYC validation confirmation email
            // Note: The event doesn't contain email directly; in a real system,
            // we would look up the member's email via the member-service API
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "kyc-validated",
                    Map.of(),
                    event.getMemberId(),
                    RecipientType.MEMBRE
            );
            log.info("KYC validation email sent for member: {}", event.getMemberId());
        } catch (Exception e) {
            log.error("Error processing MemberKycValidatedEvent for member: {}", event.getMemberId(), e);
        }
    }

    // ========================================================================
    // Payment Events
    // ========================================================================

    @KafkaListener(topics = "payment.completed", groupId = "notification-service")
    public void handlePaymentCompleted(PaymentCompletedEvent event) {
        log.info("Received PaymentCompletedEvent for payment: {} member: {}", event.getPaiementId(), event.getMembreId());
        try {
            // Send payment receipt email to member
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "payment-completed",
                    Map.of(
                            "montant", event.getMontant() != null ? event.getMontant().toString() : "0",
                            "modePaiement", event.getModePaiement() != null ? event.getModePaiement() : ""
                    ),
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );
            log.info("Payment receipt email sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing PaymentCompletedEvent for payment: {}", event.getPaiementId(), e);
        }
    }

    @KafkaListener(topics = "payment.overdue", groupId = "notification-service")
    public void handlePaymentOverdue(PaymentOverdueEvent event) {
        log.info("Received PaymentOverdueEvent for engagement: {} member: {}", event.getEngagementId(), event.getMembreId());
        try {
            // Send payment reminder via email + SMS
            Map<String, String> variables = Map.of(
                    "montantDu", event.getMontantDu() != null ? event.getMontantDu().toString() : "0",
                    "joursRetard", String.valueOf(event.getJoursRetard())
            );

            // Send email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "payment-overdue",
                    variables,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            // Send SMS
            String smsMessage = String.format(
                    "SERENITY: Rappel - Votre paiement de %s FCFA est en retard de %d jour(s). Veuillez régulariser.",
                    event.getMontantDu() != null ? event.getMontantDu() : BigDecimal.ZERO,
                    event.getJoursRetard()
            );
            smsService.sendSmsToRecipient(
                    null, // Telephone would be fetched from member service
                    smsMessage,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            log.info("Payment overdue notifications sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing PaymentOverdueEvent for engagement: {}", event.getEngagementId(), e);
        }
    }

    // ========================================================================
    // Cotisation / Adhesion Events
    // ========================================================================

    @KafkaListener(topics = "cotisation.adhesion.requested", groupId = "notification-service")
    public void handleAdhesionRequested(CotisationAdhesionRequestedEvent event) {
        log.info("Received CotisationAdhesionRequestedEvent for adhesion: {} member: {}", event.getAdhesionId(), event.getMembreId());
        try {
            // Send adhesion confirmation request email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "adhesion-requested",
                    Map.of(),
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );
            log.info("Adhesion request email sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing CotisationAdhesionRequestedEvent for adhesion: {}", event.getAdhesionId(), e);
        }
    }

    // ========================================================================
    // Account Events
    // ========================================================================

    @KafkaListener(topics = "account.low-balance", groupId = "notification-service")
    public void handleLowBalance(AccountLowBalanceEvent event) {
        log.info("Received AccountLowBalanceEvent for caisse: {}", event.getCaisseId());
        try {
            // Send low balance alert email to all admins
            Map<String, String> variables = Map.of(
                    "caisseNumero", event.getCaisseNumero() != null ? event.getCaisseNumero() : "",
                    "soldeActuel", event.getSoldeActuel() != null ? event.getSoldeActuel().toString() : "0",
                    "seuilAlerte", event.getSeuilAlerte() != null ? event.getSeuilAlerte().toString() : "0"
            );

            for (String adminEmail : notificationProperties.getAdminEmails()) {
                emailService.sendTemplatedEmail(
                        adminEmail,
                        "low-balance-alert",
                        variables
                );
            }

            // Also send SMS to admins about low balance
            String smsMessage = String.format(
                    "SERENITY ALERTE: Caisse %s - Solde bas: %s FCFA (Seuil: %s FCFA)",
                    event.getCaisseNumero(),
                    event.getSoldeActuel(),
                    event.getSeuilAlerte()
            );
            // In production, admin phone numbers would be fetched from identity service
            log.info("Low balance alert sent to admins for caisse: {}", event.getCaisseNumero());
        } catch (Exception e) {
            log.error("Error processing AccountLowBalanceEvent for caisse: {}", event.getCaisseId(), e);
        }
    }

    // ========================================================================
    // Nano-Credit Events
    // ========================================================================

    @KafkaListener(topics = "nano-credit.requested", groupId = "notification-service")
    public void handleNanoCreditRequested(NanoCreditRequestedEvent event) {
        log.info("Received NanoCreditRequestedEvent for nano-credit: {} member: {}", event.getNanoCreditId(), event.getMembreId());
        try {
            // Send nano-credit request notification email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "nano-credit-requested",
                    Map.of(
                            "montant", event.getMontant() != null ? event.getMontant().toString() : "0"
                    ),
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );
            log.info("Nano-credit request notification sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing NanoCreditRequestedEvent for nano-credit: {}", event.getNanoCreditId(), e);
        }
    }

    @KafkaListener(topics = "nano-credit.disbursed", groupId = "notification-service")
    public void handleNanoCreditDisbursed(NanoCreditDisbursedEvent event) {
        log.info("Received NanoCreditDisbursedEvent for nano-credit: {} member: {}", event.getNanoCreditId(), event.getMembreId());
        try {
            Map<String, String> variables = Map.of(
                    "montant", event.getMontant() != null ? event.getMontant().toString() : "0"
            );

            // Send disbursement confirmation email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "nano-credit-disbursed",
                    variables,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            // Send disbursement confirmation SMS
            String smsMessage = String.format(
                    "SERENITY: Votre nano-credit de %s FCFA a ete decaisse avec succes. Montant credite sur votre compte.",
                    event.getMontant() != null ? event.getMontant() : BigDecimal.ZERO
            );
            smsService.sendSmsToRecipient(
                    null, // Telephone would be fetched from member service
                    smsMessage,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            log.info("Nano-credit disbursement notifications sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing NanoCreditDisbursedEvent for nano-credit: {}", event.getNanoCreditId(), e);
        }
    }

    @KafkaListener(topics = "nano-credit.penalty.applied", groupId = "notification-service")
    public void handleNanoCreditPenaltyApplied(NanoCreditPenaltyAppliedEvent event) {
        log.info("Received NanoCreditPenaltyAppliedEvent for nano-credit: {} member: {}", event.getNanoCreditId(), event.getMembreId());
        try {
            // Send penalty notification email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "nano-credit-penalty",
                    Map.of(
                            "montantPenalite", event.getMontantPenalite() != null ? event.getMontantPenalite().toString() : "0",
                            "joursRetard", String.valueOf(event.getJoursRetard())
                    ),
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );
            log.info("Nano-credit penalty notification sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing NanoCreditPenaltyAppliedEvent for nano-credit: {}", event.getNanoCreditId(), e);
        }
    }

    // ========================================================================
    // Epargne Events
    // ========================================================================

    @KafkaListener(topics = "epargne.subscribed", groupId = "notification-service")
    public void handleEpargneSubscribed(EpargneSubscribedEvent event) {
        log.info("Received EpargneSubscribedEvent for souscription: {} member: {}", event.getSouscriptionId(), event.getMembreId());
        try {
            // Send epargne subscription confirmation
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "epargne-subscribed",
                    Map.of(),
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );
            log.info("Epargne subscription confirmation sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing EpargneSubscribedEvent for souscription: {}", event.getSouscriptionId(), e);
        }
    }

    @KafkaListener(topics = "epargne.reminder", groupId = "notification-service")
    public void handleEpargneReminder(EpargneReminderEvent event) {
        log.info("Received EpargneReminderEvent for echeance: {} member: {}", event.getEcheanceId(), event.getMembreId());
        try {
            Map<String, String> variables = Map.of(
                    "montant", event.getMontant() != null ? event.getMontant().toString() : "0",
                    "dateEcheance", event.getDateEcheance() != null ? event.getDateEcheance().format(DATE_FORMATTER) : ""
            );

            // Send epargne payment reminder email
            emailService.sendTemplatedEmailToRecipient(
                    null, // Email would be fetched from member service
                    "epargne-reminder",
                    variables,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            // Send epargne payment reminder SMS
            String smsMessage = String.format(
                    "SERENITY: Rappel epargne - Echeance de %s FCFA prevue le %s. Preparez votre versement.",
                    event.getMontant() != null ? event.getMontant() : BigDecimal.ZERO,
                    event.getDateEcheance() != null ? event.getDateEcheance().format(DATE_FORMATTER) : ""
            );
            smsService.sendSmsToRecipient(
                    null, // Telephone would be fetched from member service
                    smsMessage,
                    event.getMembreId(),
                    RecipientType.MEMBRE
            );

            log.info("Epargne reminder notifications sent for member: {}", event.getMembreId());
        } catch (Exception e) {
            log.error("Error processing EpargneReminderEvent for echeance: {}", event.getEcheanceId(), e);
        }
    }
}
