package com.serenity.collector.event;

import com.serenity.collector.entity.CollecteSession;
import com.serenity.common.event.CollectorSessionClosedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CollectorEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishSessionClosed(CollecteSession session) {
        CollectorSessionClosedEvent event = CollectorSessionClosedEvent.builder()
                .sessionId(session.getId())
                .userId(session.getUserId())
                .montantTotal(session.getMontantFermeture())
                .eventType("COLLECTOR_SESSION_CLOSED")
                .sourceService("collector-service")
                .build();

        log.info("Publishing collector.session.closed event for sessionId={}, userId={}, montantTotal={}",
                session.getId(), session.getUserId(), session.getMontantFermeture());
        kafkaTemplate.send("collector.session.closed", session.getId().toString(), event);
    }
}
