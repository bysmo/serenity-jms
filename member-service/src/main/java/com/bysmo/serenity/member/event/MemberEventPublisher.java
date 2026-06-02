package com.bysmo.serenity.member.event;

import com.bysmo.serenity.member.entity.Membre;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberEventPublisher {

    private static final String MEMBER_CREATED_TOPIC = "member.created";
    private static final String MEMBER_KYC_VALIDATED_TOPIC = "member.kyc.validated";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishMemberCreated(Membre membre) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("membreId", membre.getId());
            event.put("numero", membre.getNumero());
            event.put("nom", membre.getNom());
            event.put("prenom", membre.getPrenom());
            event.put("email", membre.getEmail());
            event.put("telephone", membre.getTelephone());
            event.put("statut", membre.getStatut().name());
            event.put("segmentId", membre.getSegmentId());
            event.put("parrainId", membre.getParrainId());
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(MEMBER_CREATED_TOPIC, membre.getId().toString(), event);
            log.info("Published MemberCreatedEvent for membre {} ({})", membre.getNumero(), membre.getId());
        } catch (Exception e) {
            log.error("Failed to publish MemberCreatedEvent for membre {}", membre.getId(), e);
        }
    }

    public void publishMemberKycValidated(UUID membreId, UUID validatedBy) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("membreId", membreId);
            event.put("validatedBy", validatedBy);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(MEMBER_KYC_VALIDATED_TOPIC, membreId.toString(), event);
            log.info("Published MemberKycValidatedEvent for membre {}", membreId);
        } catch (Exception e) {
            log.error("Failed to publish MemberKycValidatedEvent for membre {}", membreId, e);
        }
    }
}
