package com.serenity.payment.controller;

import com.serenity.payment.service.PayDunyaService;
import com.serenity.payment.service.PiSpiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
@Slf4j
public class WebhookController {

    private final PayDunyaService payDunyaService;
    private final PiSpiService piSpiService;

    /**
     * Receive PayDunya IPN (Instant Payment Notification) callback
     * This endpoint is unauthenticated - webhook endpoints don't have JWT
     */
    @PostMapping("/paydunya/ipn")
    public ResponseEntity<Void> handlePayDunyaIpn(@RequestBody Map<String, Object> payload) {
        log.info("Received PayDunya IPN callback: {}", payload);
        try {
            payDunyaService.handleIpn(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing PayDunya IPN: {}", e.getMessage(), e);
            // Return 200 anyway to prevent PayDunya from retrying
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Receive Pi-SPI webhook callback
     * This endpoint is unauthenticated - webhook endpoints don't have JWT
     */
    @PostMapping("/pispi/callback")
    public ResponseEntity<Void> handlePiSpiCallback(@RequestBody Map<String, Object> payload) {
        log.info("Received Pi-SPI webhook callback: {}", payload);
        try {
            piSpiService.handleWebhook(payload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error processing Pi-SPI webhook: {}", e.getMessage(), e);
            // Return 200 anyway to prevent Pi-SPI from retrying
            return ResponseEntity.ok().build();
        }
    }
}
