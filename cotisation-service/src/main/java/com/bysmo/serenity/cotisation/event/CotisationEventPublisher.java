package com.bysmo.serenity.cotisation.event;

import com.bysmo.serenity.cotisation.entity.CotisationAdhesion;
import com.bysmo.serenity.cotisation.entity.Engagement;
import com.bysmo.serenity.cotisation.entity.Paiement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CotisationEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentCompleted(Paiement paiement) {
        PaymentCompletedEvent event = PaymentCompletedEvent.from(
                paiement.getId(),
                paiement.getCotisationId(),
                paiement.getMembreId(),
                paiement.getMontant(),
                paiement.getModePaiement().name(),
                paiement.getStatut().name(),
                paiement.getDatePaiement()
        );
        log.info("Publishing payment.completed event for paiementId={}", paiement.getId());
        kafkaTemplate.send("payment.completed", paiement.getId().toString(), event);
    }

    public void publishPaymentOverdue(Engagement engagement) {
        PaymentOverdueEvent event = PaymentOverdueEvent.from(
                engagement.getId(),
                engagement.getCotisationId(),
                engagement.getMembreId(),
                engagement.getMontantEngage(),
                engagement.getMontantPaye(),
                engagement.getPeriodeDebut(),
                engagement.getPeriodeFin(),
                engagement.getStatut().name()
        );
        log.info("Publishing payment.overdue event for engagementId={}", engagement.getId());
        kafkaTemplate.send("payment.overdue", engagement.getId().toString(), event);
    }

    public void publishAdhesionRequested(CotisationAdhesion adhesion) {
        AdhesionRequestedEvent event = AdhesionRequestedEvent.from(
                adhesion.getId(),
                adhesion.getCotisationId(),
                adhesion.getMembreId(),
                adhesion.getStatut().name()
        );
        log.info("Publishing cotisation.adhesion.requested event for adhesionId={}", adhesion.getId());
        kafkaTemplate.send("cotisation.adhesion.requested", adhesion.getId().toString(), event);
    }
}
