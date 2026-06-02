package com.serenity.notification.service;

import com.serenity.notification.config.NotificationProperties;
import com.serenity.notification.entity.SmsGateway;
import com.serenity.notification.entity.enums.NotificationChannel;
import com.serenity.notification.entity.enums.NotificationType;
import com.serenity.notification.entity.enums.RecipientType;
import com.serenity.notification.repository.SmsGatewayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsGatewayRepository smsGatewayRepository;
    private final NotificationLogService notificationLogService;
    private final NotificationProperties notificationProperties;
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Send SMS using the active gateway providers in order of priority.
     * Falls back to the next provider if the current one fails.
     */
    public void sendSms(String telephone, String message) {
        log.info("Sending SMS to: {}", telephone);
        List<SmsGateway> activeGateways = smsGatewayRepository.findByIsActiveTrueOrderByOrdre();

        if (activeGateways.isEmpty()) {
            log.error("No active SMS gateway configured");
            notificationLogService.logFailure(
                    NotificationType.SMS, null, RecipientType.MEMBRE,
                    NotificationChannel.SMS, "SMS", message,
                    "No active SMS gateway configured"
            );
            return;
        }

        for (SmsGateway gateway : activeGateways) {
            try {
                String providerReference = sendSmsViaProvider(gateway, telephone, message);
                notificationLogService.logSuccess(
                        NotificationType.SMS, null, RecipientType.MEMBRE,
                        NotificationChannel.SMS, "SMS to " + telephone, message,
                        providerReference
                );
                log.info("SMS sent successfully to: {} via gateway: {}", telephone, gateway.getNom());
                return;
            } catch (Exception e) {
                log.warn("Failed to send SMS via gateway {}: {}", gateway.getNom(), e.getMessage());
                // Try next gateway
            }
        }

        // All gateways failed
        log.error("All SMS gateways failed for recipient: {}", telephone);
        notificationLogService.logFailure(
                NotificationType.SMS, null, RecipientType.MEMBRE,
                NotificationChannel.SMS, "SMS to " + telephone, message,
                "All SMS gateways failed"
        );
    }

    /**
     * Send SMS to a specific recipient with logging against a known recipient ID.
     */
    public void sendSmsToRecipient(String telephone, String message, UUID recipientId, RecipientType recipientType) {
        log.info("Sending SMS to: {} (recipientId: {})", telephone, recipientId);
        List<SmsGateway> activeGateways = smsGatewayRepository.findByIsActiveTrueOrderByOrdre();

        if (activeGateways.isEmpty()) {
            log.error("No active SMS gateway configured");
            notificationLogService.logFailure(
                    NotificationType.SMS, recipientId, recipientType,
                    NotificationChannel.SMS, "SMS", message,
                    "No active SMS gateway configured"
            );
            return;
        }

        for (SmsGateway gateway : activeGateways) {
            try {
                String providerReference = sendSmsViaProvider(gateway, telephone, message);
                notificationLogService.logSuccess(
                        NotificationType.SMS, recipientId, recipientType,
                        NotificationChannel.SMS, "SMS to " + telephone, message,
                        providerReference
                );
                log.info("SMS sent successfully to: {} via gateway: {} for recipient: {}",
                        telephone, gateway.getNom(), recipientId);
                return;
            } catch (Exception e) {
                log.warn("Failed to send SMS via gateway {}: {}", gateway.getNom(), e.getMessage());
            }
        }

        log.error("All SMS gateways failed for recipient: {}", telephone);
        notificationLogService.logFailure(
                NotificationType.SMS, recipientId, recipientType,
                NotificationChannel.SMS, "SMS to " + telephone, message,
                "All SMS gateways failed"
        );
    }

    /**
     * Send SMS via a specific gateway provider.
     * Supports Orange CI and MTN CI API formats.
     */
    private String sendSmsViaProvider(SmsGateway gateway, String telephone, String message) {
        log.debug("Sending SMS via provider: {} to: {}", gateway.getProviderCode(), telephone);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(gateway.getApiKey());

        String payload;
        if ("orange".equalsIgnoreCase(gateway.getProviderCode())) {
            payload = buildOrangeSmsPayload(gateway, telephone, message);
        } else if ("mtn".equalsIgnoreCase(gateway.getProviderCode())) {
            payload = buildMtnSmsPayload(gateway, telephone, message);
        } else {
            payload = buildGenericSmsPayload(gateway, telephone, message);
        }

        HttpEntity<String> request = new HttpEntity<>(payload, headers);

        try {
            String response = restTemplate.postForObject(
                    gateway.getApiUrl() + "/messages",
                    request,
                    String.class
            );
            log.debug("SMS API response from {}: {}", gateway.getProviderCode(), response);
            return gateway.getProviderCode() + "-" + System.currentTimeMillis();
        } catch (Exception e) {
            log.error("SMS API call failed for provider {}: {}", gateway.getProviderCode(), e.getMessage());
            throw new RuntimeException("SMS provider " + gateway.getProviderCode() + " failed: " + e.getMessage(), e);
        }
    }

    /**
     * Build Orange CI SMS API payload.
     * Orange CI API format: outboundMessage with senderAddress and address.
     */
    private String buildOrangeSmsPayload(SmsGateway gateway, String telephone, String message) {
        String formattedPhone = formatPhoneNumber(telephone);
        return String.format(
                "{\"outboundSMSMessageRequest\":{\"address\":\"tel:%s\",\"senderAddress\":\"tel:%s\",\"outboundSMSTextMessage\":{\"message\":\"%s\"}}}",
                formattedPhone, gateway.getSenderName(), escapeJson(message)
        );
    }

    /**
     * Build MTN CI SMS API payload.
     * MTN CI API format: simple JSON with to, from, message.
     */
    private String buildMtnSmsPayload(SmsGateway gateway, String telephone, String message) {
        String formattedPhone = formatPhoneNumber(telephone);
        return String.format(
                "{\"to\":\"%s\",\"from\":\"%s\",\"message\":\"%s\"}",
                formattedPhone, gateway.getSenderName(), escapeJson(message)
        );
    }

    /**
     * Build generic SMS API payload for other providers.
     */
    private String buildGenericSmsPayload(SmsGateway gateway, String telephone, String message) {
        String formattedPhone = formatPhoneNumber(telephone);
        return String.format(
                "{\"to\":\"%s\",\"from\":\"%s\",\"text\":\"%s\"}",
                formattedPhone, gateway.getSenderName(), escapeJson(message)
        );
    }

    /**
     * Format phone number to international format (Côte d'Ivoire).
     */
    private String formatPhoneNumber(String telephone) {
        if (telephone == null) {
            return "";
        }
        String cleaned = telephone.replaceAll("[\\s\\-\\.\\(\\)]", "");
        if (cleaned.startsWith("0")) {
            cleaned = "+225" + cleaned.substring(1);
        } else if (cleaned.startsWith("225")) {
            cleaned = "+" + cleaned;
        } else if (!cleaned.startsWith("+")) {
            cleaned = "+225" + cleaned;
        }
        return cleaned;
    }

    /**
     * Escape special characters for JSON string values.
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
