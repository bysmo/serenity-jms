package com.bysmo.serenity.epargne.event;

import com.bysmo.serenity.epargne.entity.EpargneEcheance;
import com.bysmo.serenity.epargne.entity.EpargneSouscription;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class EpargneKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishEpargneSubscribed(EpargneSouscription souscription) {
        EpargneSubscribedEvent event = new EpargneSubscribedEvent(
                souscription.getId(),
                souscription.getMembreId(),
                souscription.getPlanId(),
                souscription.getPlan() != null ? souscription.getPlan().getNom() : null,
                souscription.getMontant(),
                souscription.getPlan() != null ? souscription.getPlan().getFrequence().name() : null,
                souscription.getDateSouscription(),
                souscription.getCaisseId()
        );

        log.info("Publishing EpargneSubscribedEvent for souscription={}, membre={}",
                souscription.getId(), souscription.getMembreId());
        kafkaTemplate.send("epargne.subscribed", souscription.getId().toString(), event);
    }

    public void publishEpargneReminder(EpargneEcheance echeance, UUID membreId) {
        EpargneSouscription souscription = echeance.getSouscription();
        BigDecimal montantRestant = echeance.getMontant().subtract(echeance.getMontantPaye());

        EpargneReminderEvent event = new EpargneReminderEvent(
                echeance.getId(),
                echeance.getSouscriptionId(),
                membreId,
                souscription != null && souscription.getPlan() != null ? souscription.getPlanId() : null,
                souscription != null && souscription.getPlan() != null ? souscription.getPlan().getNom() : null,
                echeance.getMontant(),
                echeance.getDateEcheance(),
                souscription != null && souscription.getPlan() != null ? souscription.getPlan().getFrequence().name() : null,
                echeance.getMontantPaye(),
                montantRestant
        );

        log.info("Publishing EpargneReminderEvent for echeance={}, membre={}",
                echeance.getId(), membreId);
        kafkaTemplate.send("epargne.reminder", echeance.getId().toString(), event);
    }
}
