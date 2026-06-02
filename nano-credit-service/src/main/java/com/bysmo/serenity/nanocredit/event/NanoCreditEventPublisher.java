package com.bysmo.serenity.nanocredit.event;

import com.bysmo.serenity.common.event.NanoCreditDisbursedEvent;
import com.bysmo.serenity.common.event.NanoCreditPenaltyAppliedEvent;
import com.bysmo.serenity.common.event.NanoCreditRequestedEvent;
import com.bysmo.serenity.nanocredit.entity.NanoCredit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class NanoCreditEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishNanoCreditRequested(NanoCredit credit) {
        NanoCreditRequestedEvent event = NanoCreditRequestedEvent.builder()
                .nanoCreditId(credit.getId())
                .membreId(credit.getMembreId())
                .montant(credit.getMontant())
                .eventType("NANO_CREDIT_REQUESTED")
                .sourceService("nano-credit-service")
                .build();

        log.info("Publishing nano-credit.requested event for creditId={}, membreId={}, montant={}",
                credit.getId(), credit.getMembreId(), credit.getMontant());
        kafkaTemplate.send("nano-credit.requested", credit.getId().toString(), event);
    }

    public void publishNanoCreditDisbursed(NanoCredit credit) {
        NanoCreditDisbursedEvent event = NanoCreditDisbursedEvent.builder()
                .nanoCreditId(credit.getId())
                .membreId(credit.getMembreId())
                .montant(credit.getMontant())
                .compteCreditId(credit.getCompteCreditId())
                .eventType("NANO_CREDIT_DISBURSED")
                .sourceService("nano-credit-service")
                .build();

        log.info("Publishing nano-credit.disbursed event for creditId={}, membreId={}, montant={}",
                credit.getId(), credit.getMembreId(), credit.getMontant());
        kafkaTemplate.send("nano-credit.disbursed", credit.getId().toString(), event);
    }

    public void publishNanoCreditPenaltyApplied(NanoCredit credit, BigDecimal montantPenalite, int joursRetard) {
        NanoCreditPenaltyAppliedEvent event = NanoCreditPenaltyAppliedEvent.builder()
                .nanoCreditId(credit.getId())
                .membreId(credit.getMembreId())
                .montantPenalite(montantPenalite)
                .joursRetard(joursRetard)
                .eventType("NANO_CREDIT_PENALTY_APPLIED")
                .sourceService("nano-credit-service")
                .build();

        log.info("Publishing nano-credit.penalty-applied event for creditId={}, montantPenalite={}, joursRetard={}",
                credit.getId(), montantPenalite, joursRetard);
        kafkaTemplate.send("nano-credit.penalty-applied", credit.getId().toString(), event);
    }
}
