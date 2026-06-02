package com.serenity.payment.service;

import com.serenity.payment.config.PiSpiProperties;
import com.serenity.payment.entity.PiSpiConfiguration;
import com.serenity.payment.entity.PaymentTransaction;
import com.serenity.payment.entity.enums.PaymentGateway;
import com.serenity.payment.entity.enums.TransactionStatut;
import com.serenity.payment.entity.enums.TransactionType;
import com.serenity.payment.repository.PiSpiConfigurationRepository;
import com.serenity.payment.repository.PaymentTransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PiSpiService {

    private final PiSpiProperties piSpiProperties;
    private final PiSpiConfigurationRepository piSpiConfigurationRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Send money (disbursement) via Pi-SPI
     * POST to /send
     */
    public PaymentTransaction sendMoney(String telephone, BigDecimal montant, String withdrawMode, String internalReference) {
        log.info("Processing Pi-SPI send money: telephone={}, montant={}, withdrawMode={}", telephone, montant, withdrawMode);

        String reference = generateReference("DIS");
        PaymentTransaction transaction = PaymentTransaction.builder()
                .id(UUID.randomUUID())
                .reference(reference)
                .gateway(PaymentGateway.PISPI)
                .transactionType(TransactionType.DISBURSEMENT)
                .statut(TransactionStatut.PENDING)
                .telephone(telephone)
                .montant(montant)
                .currency("XOF")
                .withdrawMode(withdrawMode)
                .internalReference(internalReference)
                .build();

        try {
            HttpHeaders headers = buildPiSpiHeaders();
            Map<String, Object> body = buildSendMoneyBody(telephone, montant, withdrawMode, reference);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = piSpiProperties.getBaseUrl() + "/send";

            log.debug("Sending disbursement request to Pi-SPI: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String status = responseJson.path("status").asText();
                String externalRef = responseJson.path("reference").asText();

                transaction.setExternalReference(externalRef != null && !externalRef.isEmpty() ? externalRef : reference);

                Map<String, Object> gatewayResponse = new HashMap<>();
                gatewayResponse.put("status", status);
                gatewayResponse.put("reference", externalRef);
                gatewayResponse.put("message", responseJson.path("message").asText());
                gatewayResponse.put("transaction_id", responseJson.path("transaction_id").asText());

                if ("success".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                    // The actual transfer may take time - keep as PENDING
                    if ("success".equalsIgnoreCase(status)) {
                        transaction.setStatut(TransactionStatut.SUCCESS);
                        transaction.setConfirmedAt(LocalDateTime.now());
                        log.info("Pi-SPI send money SUCCESS: externalRef={}", externalRef);
                    } else {
                        log.info("Pi-SPI send money PENDING: externalRef={}", externalRef);
                    }
                } else {
                    String errorMessage = responseJson.path("message").asText("Unknown error");
                    transaction.setStatut(TransactionStatut.FAILED);
                    transaction.setErrorMessage(errorMessage);
                    transaction.setFailedAt(LocalDateTime.now());
                    log.error("Pi-SPI send money failed: status={}, message={}", status, errorMessage);
                }

                transaction.setGatewayResponse(gatewayResponse);
            } else {
                transaction.setStatut(TransactionStatut.FAILED);
                transaction.setErrorMessage("Pi-SPI API returned non-success status: " + response.getStatusCode());
                transaction.setFailedAt(LocalDateTime.now());
                log.error("Pi-SPI API returned non-success status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error processing Pi-SPI send money: {}", e.getMessage(), e);
            transaction.setStatut(TransactionStatut.FAILED);
            transaction.setErrorMessage("Pi-SPI API error: " + e.getMessage());
            transaction.setFailedAt(LocalDateTime.now());

            Map<String, Object> gatewayResponse = new HashMap<>();
            gatewayResponse.put("error", e.getMessage());
            transaction.setGatewayResponse(gatewayResponse);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Process collection via Pi-SPI
     * POST to /collect
     */
    public PaymentTransaction collect(String telephone, BigDecimal montant, String internalReference) {
        log.info("Processing Pi-SPI collection: telephone={}, montant={}", telephone, montant);

        String reference = generateReference("COL");
        PaymentTransaction transaction = PaymentTransaction.builder()
                .id(UUID.randomUUID())
                .reference(reference)
                .gateway(PaymentGateway.PISPI)
                .transactionType(TransactionType.COLLECTION)
                .statut(TransactionStatut.PENDING)
                .telephone(telephone)
                .montant(montant)
                .currency("XOF")
                .internalReference(internalReference)
                .build();

        try {
            HttpHeaders headers = buildPiSpiHeaders();
            Map<String, Object> body = buildCollectBody(telephone, montant, reference);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = piSpiProperties.getBaseUrl() + "/collect";

            log.debug("Sending collection request to Pi-SPI: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String status = responseJson.path("status").asText();
                String externalRef = responseJson.path("reference").asText();

                transaction.setExternalReference(externalRef != null && !externalRef.isEmpty() ? externalRef : reference);

                Map<String, Object> gatewayResponse = new HashMap<>();
                gatewayResponse.put("status", status);
                gatewayResponse.put("reference", externalRef);
                gatewayResponse.put("message", responseJson.path("message").asText());
                gatewayResponse.put("transaction_id", responseJson.path("transaction_id").asText());

                if ("success".equalsIgnoreCase(status) || "pending".equalsIgnoreCase(status)) {
                    if ("success".equalsIgnoreCase(status)) {
                        transaction.setStatut(TransactionStatut.SUCCESS);
                        transaction.setConfirmedAt(LocalDateTime.now());
                        log.info("Pi-SPI collection SUCCESS: externalRef={}", externalRef);
                    } else {
                        log.info("Pi-SPI collection PENDING: externalRef={}", externalRef);
                    }
                } else {
                    String errorMessage = responseJson.path("message").asText("Unknown error");
                    transaction.setStatut(TransactionStatut.FAILED);
                    transaction.setErrorMessage(errorMessage);
                    transaction.setFailedAt(LocalDateTime.now());
                    log.error("Pi-SPI collection failed: status={}, message={}", status, errorMessage);
                }

                transaction.setGatewayResponse(gatewayResponse);
            } else {
                transaction.setStatut(TransactionStatut.FAILED);
                transaction.setErrorMessage("Pi-SPI API returned non-success status: " + response.getStatusCode());
                transaction.setFailedAt(LocalDateTime.now());
            }
        } catch (Exception e) {
            log.error("Error processing Pi-SPI collection: {}", e.getMessage(), e);
            transaction.setStatut(TransactionStatut.FAILED);
            transaction.setErrorMessage("Pi-SPI API error: " + e.getMessage());
            transaction.setFailedAt(LocalDateTime.now());

            Map<String, Object> gatewayResponse = new HashMap<>();
            gatewayResponse.put("error", e.getMessage());
            transaction.setGatewayResponse(gatewayResponse);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Check transaction status via Pi-SPI
     * GET /status/{reference}
     */
    public PaymentTransaction checkStatus(String externalReference) {
        log.info("Checking Pi-SPI transaction status: externalRef={}", externalReference);

        PaymentTransaction transaction = paymentTransactionRepository.findByExternalReference(externalReference)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with external reference: " + externalReference));

        try {
            HttpHeaders headers = buildPiSpiHeaders();
            String url = piSpiProperties.getBaseUrl() + "/status/" + externalReference;

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String status = responseJson.path("status").asText();

                Map<String, Object> gatewayResponse = new HashMap<>();
                gatewayResponse.put("status", status);
                gatewayResponse.put("message", responseJson.path("message").asText());
                gatewayResponse.put("transaction_id", responseJson.path("transaction_id").asText());

                switch (status.toLowerCase()) {
                    case "success", "completed" -> {
                        transaction.setStatut(TransactionStatut.SUCCESS);
                        transaction.setConfirmedAt(LocalDateTime.now());
                        log.info("Pi-SPI transaction SUCCESS: externalRef={}", externalReference);
                    }
                    case "failed", "cancelled", "rejected" -> {
                        transaction.setStatut(TransactionStatut.FAILED);
                        transaction.setErrorMessage(responseJson.path("message").asText(status));
                        transaction.setFailedAt(LocalDateTime.now());
                        log.warn("Pi-SPI transaction FAILED: externalRef={}, reason={}", externalReference, status);
                    }
                    default -> log.debug("Pi-SPI transaction still PENDING: externalRef={}, status={}", externalReference, status);
                }

                // Update fees if provided
                JsonNode feesNode = responseJson.path("fees");
                if (!feesNode.isMissingNode()) {
                    try {
                        transaction.setFees(new BigDecimal(feesNode.asText()));
                    } catch (NumberFormatException e) {
                        log.warn("Pi-SPI: Invalid fees format: {}", feesNode);
                    }
                }

                transaction.setGatewayResponse(gatewayResponse);
            }
        } catch (Exception e) {
            log.error("Error checking Pi-SPI transaction status: externalRef={}, error={}", externalReference, e.getMessage(), e);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Handle Pi-SPI webhook callback
     */
    public void handleWebhook(Map<String, Object> payload) {
        log.info("Handling Pi-SPI webhook callback: {}", payload);

        try {
            String externalRef = (String) payload.get("reference");
            if (externalRef == null) {
                // Try alternative field names
                externalRef = (String) payload.get("transaction_reference");
            }
            if (externalRef == null) {
                log.error("Pi-SPI webhook missing reference field: {}", payload);
                return;
            }

            PaymentTransaction transaction = paymentTransactionRepository.findByExternalReference(externalRef)
                    .orElse(null);

            if (transaction == null) {
                log.warn("Pi-SPI webhook: No transaction found for reference={}", externalRef);
                return;
            }

            // Store full callback data
            transaction.setCallbackData(payload);

            // Parse status from webhook
            String status = (String) payload.get("status");
            if (status != null) {
                switch (status.toLowerCase()) {
                    case "success", "completed" -> {
                        transaction.setStatut(TransactionStatut.SUCCESS);
                        transaction.setConfirmedAt(LocalDateTime.now());
                        log.info("Pi-SPI webhook: Transaction SUCCESS: ref={}", transaction.getReference());
                    }
                    case "failed", "cancelled", "rejected" -> {
                        transaction.setStatut(TransactionStatut.FAILED);
                        String message = (String) payload.get("message");
                        transaction.setErrorMessage(message != null ? message : status);
                        transaction.setFailedAt(LocalDateTime.now());
                        log.warn("Pi-SPI webhook: Transaction FAILED: ref={}", transaction.getReference());
                    }
                    default -> log.debug("Pi-SPI webhook: Unknown status '{}' for ref={}", status, transaction.getReference());
                }
            }

            // Update montant if provided
            Object montantObj = payload.get("amount");
            if (montantObj != null) {
                try {
                    transaction.setMontant(new BigDecimal(montantObj.toString()));
                } catch (NumberFormatException e) {
                    log.warn("Pi-SPI webhook: Invalid amount format: {}", montantObj);
                }
            }

            // Update fees if provided
            Object feesObj = payload.get("fees");
            if (feesObj != null) {
                try {
                    transaction.setFees(new BigDecimal(feesObj.toString()));
                } catch (NumberFormatException e) {
                    log.warn("Pi-SPI webhook: Invalid fee format: {}", feesObj);
                }
            }

            // Calculate net amount
            if (transaction.getMontant() != null && transaction.getFees() != null) {
                if (transaction.getTransactionType() == TransactionType.DISBURSEMENT) {
                    transaction.setNetAmount(transaction.getMontant().subtract(transaction.getFees()));
                } else {
                    transaction.setNetAmount(transaction.getMontant().subtract(transaction.getFees()));
                }
            }

            paymentTransactionRepository.save(transaction);
            log.info("Pi-SPI webhook processed successfully for transaction: ref={}", transaction.getReference());

        } catch (Exception e) {
            log.error("Error processing Pi-SPI webhook: {}", e.getMessage(), e);
        }
    }

    /**
     * Build Pi-SPI API headers with authentication
     */
    private HttpHeaders buildPiSpiHeaders() {
        // Try to get configuration from DB first, fall back to properties
        PiSpiConfiguration config = piSpiConfigurationRepository.findByIsActive(true)
                .orElse(null);

        String clientId = config != null ? config.getClientId() : piSpiProperties.getClientId();
        String clientSecret = config != null ? config.getClientSecret() : piSpiProperties.getClientSecret();
        String apiKey = config != null ? config.getApiKey() : piSpiProperties.getApiKey();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Client-Id", clientId);
        headers.set("X-Client-Secret", clientSecret);
        headers.set("X-Api-Key", apiKey);

        return headers;
    }

    /**
     * Build send money request body
     */
    private Map<String, Object> buildSendMoneyBody(String telephone, BigDecimal montant, String withdrawMode, String reference) {
        Map<String, Object> body = new HashMap<>();
        body.put("phone", telephone);
        body.put("amount", montant);
        body.put("withdraw_mode", withdrawMode);
        body.put("reference", reference);
        body.put("currency", "XOF");
        body.put("paye_alias", piSpiProperties.getPayeAlias());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("internal_reference", reference);
        metadata.put("source", "serenity-jms");
        body.put("metadata", metadata);

        return body;
    }

    /**
     * Build collect request body
     */
    private Map<String, Object> buildCollectBody(String telephone, BigDecimal montant, String reference) {
        Map<String, Object> body = new HashMap<>();
        body.put("phone", telephone);
        body.put("amount", montant);
        body.put("reference", reference);
        body.put("currency", "XOF");
        body.put("paye_alias", piSpiProperties.getPayeAlias());

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("internal_reference", reference);
        metadata.put("source", "serenity-jms");
        body.put("metadata", metadata);

        return body;
    }

    /**
     * Generate a unique transaction reference
     */
    private String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + System.currentTimeMillis() % 10000;
    }
}
