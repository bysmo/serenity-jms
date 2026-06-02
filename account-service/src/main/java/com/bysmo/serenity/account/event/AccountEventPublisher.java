package com.bysmo.serenity.account.event;

import com.bysmo.serenity.common.event.AccountLowBalanceEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventPublisher {

    private static final String LOW_BALANCE_TOPIC = "account.low-balance";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishLowBalance(UUID caisseId, String caisseNumero, BigDecimal soldeActuel, BigDecimal seuilAlerte) {
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("caisseId", caisseId);
            event.put("caisseNumero", caisseNumero);
            event.put("soldeActuel", soldeActuel);
            event.put("seuilAlerte", seuilAlerte);
            event.put("timestamp", LocalDateTime.now().toString());

            kafkaTemplate.send(LOW_BALANCE_TOPIC, caisseId.toString(), event);
            log.warn("Published AccountLowBalanceEvent for caisse {} ({}): solde={}, seuil={}",
                    caisseNumero, caisseId, soldeActuel, seuilAlerte);
        } catch (Exception e) {
            log.error("Failed to publish AccountLowBalanceEvent for caisse {}: {}", caisseId, e.getMessage(), e);
        }
    }
}
