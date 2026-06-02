package com.bysmo.serenity.payment.service;

import com.bysmo.serenity.payment.config.PayDunyaProperties;
import com.bysmo.serenity.payment.entity.PayDunyaConfiguration;
import com.bysmo.serenity.payment.entity.PaymentTransaction;
import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import com.bysmo.serenity.payment.entity.enums.TransactionStatut;
import com.bysmo.serenity.payment.entity.enums.TransactionType;
import com.bysmo.serenity.payment.repository.PayDunyaConfigurationRepository;
import com.bysmo.serenity.payment.repository.PaymentTransactionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PayDunyaService {

    private final PayDunyaProperties payDunyaProperties;
    private final PayDunyaConfigurationRepository payDunyaConfigurationRepository;
    private final PaymentTransactionRepository paymentTransactionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Initialize a payment/checkout via PayDunya
     * POST to /checkout-invoice/create
     */
    public PaymentTransaction createCheckout(BigDecimal montant, String description, String internalReference) {
        log.info("Creating PayDunya checkout for amount={}, internalRef={}", montant, internalReference);

        String reference = generateReference("COL");
        PaymentTransaction transaction = PaymentTransaction.builder()
                .id(UUID.randomUUID())
                .reference(reference)
                .gateway(PaymentGateway.PAYDUNYA)
                .transactionType(TransactionType.COLLECTION)
                .statut(TransactionStatut.PENDING)
                .montant(montant)
                .currency("XOF")
                .description(description)
                .internalReference(internalReference)
                .build();

        try {
            HttpHeaders headers = buildPayDunyaHeaders();
            Map<String, Object> body = buildCheckoutBody(montant, description, reference);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = payDunyaProperties.getBaseUrl() + "/checkout-invoice/create";

            log.debug("Sending checkout request to PayDunya: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String responseCode = responseJson.path("response_code").asText();

                if ("00".equals(responseCode)) {
                    String token = responseJson.path("token").asText();
                    String invoiceUrl = responseJson.path("response_text").asText();

                    transaction.setExternalReference(token);

                    Map<String, Object> gatewayResponse = new HashMap<>();
                    gatewayResponse.put("token", token);
                    gatewayResponse.put("invoice_url", invoiceUrl);
                    gatewayResponse.put("response_code", responseCode);
                    gatewayResponse.put("response_text", responseJson.path("response_text").asText());
                    transaction.setGatewayResponse(gatewayResponse);

                    log.info("PayDunya checkout created successfully: token={}, ref={}", token, reference);
                } else {
                    String errorMessage = responseJson.path("response_text").asText("Unknown error");
                    transaction.setStatut(TransactionStatut.FAILED);
                    transaction.setErrorMessage(errorMessage);
                    transaction.setFailedAt(LocalDateTime.now());

                    Map<String, Object> gatewayResponse = new HashMap<>();
                    gatewayResponse.put("response_code", responseCode);
                    gatewayResponse.put("response_text", errorMessage);
                    transaction.setGatewayResponse(gatewayResponse);

                    log.error("PayDunya checkout failed: code={}, message={}", responseCode, errorMessage);
                }
            } else {
                transaction.setStatut(TransactionStatut.FAILED);
                transaction.setErrorMessage("PayDunya API returned non-success status: " + response.getStatusCode());
                transaction.setFailedAt(LocalDateTime.now());
                log.error("PayDunya API returned non-success status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error creating PayDunya checkout: {}", e.getMessage(), e);
            transaction.setStatut(TransactionStatut.FAILED);
            transaction.setErrorMessage("PayDunya API error: " + e.getMessage());
            transaction.setFailedAt(LocalDateTime.now());

            Map<String, Object> gatewayResponse = new HashMap<>();
            gatewayResponse.put("error", e.getMessage());
            transaction.setGatewayResponse(gatewayResponse);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Process disbursement (cashout) via PayDunya
     * POST to /disburse/get-invoice
     */
    public PaymentTransaction disburse(String telephone, BigDecimal montant, String withdrawMode, String internalReference) {
        log.info("Processing PayDunya disbursement: telephone={}, montant={}, withdrawMode={}", telephone, montant, withdrawMode);

        String reference = generateReference("DIS");
        PaymentTransaction transaction = PaymentTransaction.builder()
                .id(UUID.randomUUID())
                .reference(reference)
                .gateway(PaymentGateway.PAYDUNYA)
                .transactionType(TransactionType.DISBURSEMENT)
                .statut(TransactionStatut.PENDING)
                .telephone(telephone)
                .montant(montant)
                .currency("XOF")
                .withdrawMode(withdrawMode)
                .internalReference(internalReference)
                .build();

        try {
            HttpHeaders headers = buildPayDunyaHeaders();
            Map<String, Object> body = buildDisbursementBody(telephone, montant, withdrawMode, reference);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = payDunyaProperties.getBaseUrl() + "/disburse/get-invoice";

            log.debug("Sending disbursement request to PayDunya: {}", url);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String responseCode = responseJson.path("response_code").asText();

                if ("00".equals(responseCode)) {
                    String disburseToken = responseJson.path("token").asText();
                    transaction.setExternalReference(disburseToken);

                    Map<String, Object> gatewayResponse = new HashMap<>();
                    gatewayResponse.put("token", disburseToken);
                    gatewayResponse.put("response_code", responseCode);
                    gatewayResponse.put("response_text", responseJson.path("response_text").asText());
                    transaction.setGatewayResponse(gatewayResponse);

                    // Now confirm the disbursement
                    confirmDisbursement(disburseToken, transaction);
                } else {
                    String errorMessage = responseJson.path("response_text").asText("Unknown error");
                    transaction.setStatut(TransactionStatut.FAILED);
                    transaction.setErrorMessage(errorMessage);
                    transaction.setFailedAt(LocalDateTime.now());

                    Map<String, Object> gatewayResponse = new HashMap<>();
                    gatewayResponse.put("response_code", responseCode);
                    gatewayResponse.put("response_text", errorMessage);
                    transaction.setGatewayResponse(gatewayResponse);

                    log.error("PayDunya disbursement failed: code={}, message={}", responseCode, errorMessage);
                }
            } else {
                transaction.setStatut(TransactionStatut.FAILED);
                transaction.setErrorMessage("PayDunya API returned non-success status: " + response.getStatusCode());
                transaction.setFailedAt(LocalDateTime.now());
                log.error("PayDunya API returned non-success status: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error processing PayDunya disbursement: {}", e.getMessage(), e);
            transaction.setStatut(TransactionStatut.FAILED);
            transaction.setErrorMessage("PayDunya API error: " + e.getMessage());
            transaction.setFailedAt(LocalDateTime.now());

            Map<String, Object> gatewayResponse = new HashMap<>();
            gatewayResponse.put("error", e.getMessage());
            transaction.setGatewayResponse(gatewayResponse);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Confirm a disbursement after getting the invoice
     * POST to /disburse/submit-invoice/{disburse_token}
     */
    private void confirmDisbursement(String disburseToken, PaymentTransaction transaction) {
        try {
            HttpHeaders headers = buildPayDunyaHeaders();
            Map<String, Object> body = new HashMap<>();
            body.put("disburse_token", disburseToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = payDunyaProperties.getBaseUrl() + "/disburse/submit-invoice/" + disburseToken;

            log.debug("Confirming PayDunya disbursement: token={}", disburseToken);
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String responseCode = responseJson.path("response_code").asText();

                if ("00".equals(responseCode)) {
                    log.info("PayDunya disbursement confirmed successfully: token={}", disburseToken);
                    // Transaction remains PENDING - will be updated via IPN or polling
                } else {
                    String errorMessage = responseJson.path("response_text").asText("Disbursement confirmation failed");
                    transaction.setStatut(TransactionStatut.FAILED);
                    transaction.setErrorMessage(errorMessage);
                    transaction.setFailedAt(LocalDateTime.now());
                    log.error("PayDunya disbursement confirmation failed: code={}, message={}", responseCode, errorMessage);
                }
            }
        } catch (Exception e) {
            log.error("Error confirming PayDunya disbursement: {}", e.getMessage(), e);
            // Don't override status here - the disbursement might still be processing
        }
    }

    /**
     * Verify payment status via PayDunya
     * GET /checkout-invoice/confirm/{token}
     */
    public PaymentTransaction verifyPayment(String externalReference) {
        log.info("Verifying PayDunya payment: externalRef={}", externalReference);

        PaymentTransaction transaction = paymentTransactionRepository.findByExternalReference(externalReference)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found with external reference: " + externalReference));

        try {
            HttpHeaders headers = buildPayDunyaHeaders();
            String url = payDunyaProperties.getBaseUrl() + "/checkout-invoice/confirm/" + externalReference;

            HttpEntity<Void> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                String responseCode = responseJson.path("response_code").asText();

                if ("00".equals(responseCode)) {
                    transaction.setStatut(TransactionStatut.SUCCESS);
                    transaction.setConfirmedAt(LocalDateTime.now());

                    Map<String, Object> gatewayResponse = new HashMap<>();
                    gatewayResponse.put("response_code", responseCode);
                    gatewayResponse.put("receipt_url", responseJson.path("receipt_url").asText());
                    gatewayResponse.put("custom_data", responseJson.path("custom_data").asText());

                    // Parse customer info if available
                    JsonNode customerNode = responseJson.path("customer");
                    if (!customerNode.isMissingNode()) {
                        Map<String, Object> customerInfo = new HashMap<>();
                        customerInfo.put("name", customerNode.path("name").asText());
                        customerInfo.put("phone", customerNode.path("phone").asText());
                        customerInfo.put("email", customerNode.path("email").asText());
                        gatewayResponse.put("customer", customerInfo);
                    }

                    transaction.setGatewayResponse(gatewayResponse);
                    log.info("PayDunya payment verified as SUCCESS: externalRef={}", externalReference);
                } else {
                    // Payment not yet completed or failed
                    String statusText = responseJson.path("response_text").asText();
                    if ("fail".equalsIgnoreCase(statusText) || "cancelled".equalsIgnoreCase(statusText)) {
                        transaction.setStatut(TransactionStatut.FAILED);
                        transaction.setErrorMessage(statusText);
                        transaction.setFailedAt(LocalDateTime.now());
                        log.warn("PayDunya payment FAILED: externalRef={}, reason={}", externalReference, statusText);
                    } else {
                        log.debug("PayDunya payment still PENDING: externalRef={}, status={}", externalReference, statusText);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error verifying PayDunya payment: externalRef={}, error={}", externalReference, e.getMessage(), e);
        }

        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Handle IPN (Instant Payment Notification) callback from PayDunya
     */
    public void handleIpn(Map<String, Object> payload) {
        log.info("Handling PayDunya IPN callback: {}", payload);

        try {
            String externalRef = (String) payload.get("token");
            if (externalRef == null) {
                log.error("PayDunya IPN missing token field: {}", payload);
                return;
            }

            PaymentTransaction transaction = paymentTransactionRepository.findByExternalReference(externalRef)
                    .orElse(null);

            if (transaction == null) {
                log.warn("PayDunya IPN: No transaction found for token={}", externalRef);
                return;
            }

            // Store full callback data
            transaction.setCallbackData(payload);

            // Parse status from IPN
            String status = (String) payload.get("status");
            if (status != null) {
                switch (status.toLowerCase()) {
                    case "completed", "success" -> {
                        transaction.setStatut(TransactionStatut.SUCCESS);
                        transaction.setConfirmedAt(LocalDateTime.now());
                        log.info("PayDunya IPN: Transaction SUCCESS: ref={}", transaction.getReference());
                    }
                    case "failed", "cancelled" -> {
                        transaction.setStatut(TransactionStatut.FAILED);
                        transaction.setErrorMessage(status);
                        transaction.setFailedAt(LocalDateTime.now());
                        log.warn("PayDunya IPN: Transaction FAILED: ref={}", transaction.getReference());
                    }
                    default -> log.debug("PayDunya IPN: Unknown status '{}' for ref={}", status, transaction.getReference());
                }
            }

            // Update montant if provided
            Object montantObj = payload.get("amount");
            if (montantObj != null) {
                try {
                    transaction.setMontant(new BigDecimal(montantObj.toString()));
                } catch (NumberFormatException e) {
                    log.warn("PayDunya IPN: Invalid amount format: {}", montantObj);
                }
            }

            // Update fees if provided
            Object feesObj = payload.get("fee");
            if (feesObj != null) {
                try {
                    transaction.setFees(new BigDecimal(feesObj.toString()));
                } catch (NumberFormatException e) {
                    log.warn("PayDunya IPN: Invalid fee format: {}", feesObj);
                }
            }

            paymentTransactionRepository.save(transaction);
            log.info("PayDunya IPN processed successfully for transaction: ref={}", transaction.getReference());

        } catch (Exception e) {
            log.error("Error processing PayDunya IPN: {}", e.getMessage(), e);
        }
    }

    /**
     * Check pending transactions older than 5 minutes - runs every 15 minutes
     */
    @Scheduled(cron = "0 */15 * * * ?")
    public void checkPendingTransactions() {
        log.info("Checking pending PayDunya transactions...");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<PaymentTransaction> pendingTransactions = paymentTransactionRepository
                .findByStatutAndCreatedAtBefore(TransactionStatut.PENDING, threshold)
                .stream()
                .filter(t -> t.getGateway() == PaymentGateway.PAYDUNYA)
                .toList();

        log.info("Found {} pending PayDunya transactions older than 5 minutes", pendingTransactions.size());

        for (PaymentTransaction transaction : pendingTransactions) {
            try {
                if (transaction.getExternalReference() != null) {
                    log.debug("Verifying pending transaction: ref={}, externalRef={}",
                            transaction.getReference(), transaction.getExternalReference());
                    verifyPayment(transaction.getExternalReference());
                } else {
                    log.warn("Pending transaction without external reference: ref={}", transaction.getReference());
                    // Mark as failed if pending too long without external reference
                    if (transaction.getCreatedAt().isBefore(LocalDateTime.now().minusHours(1))) {
                        transaction.setStatut(TransactionStatut.FAILED);
                        transaction.setErrorMessage("Transaction expired - no external reference received");
                        transaction.setFailedAt(LocalDateTime.now());
                        paymentTransactionRepository.save(transaction);
                    }
                }
            } catch (Exception e) {
                log.error("Error verifying pending transaction ref={}: {}",
                        transaction.getReference(), e.getMessage());
            }
        }
    }

    /**
     * Build PayDunya API headers with authentication
     */
    private HttpHeaders buildPayDunyaHeaders() {
        // Try to get configuration from DB first, fall back to properties
        PayDunyaConfiguration config = payDunyaConfigurationRepository.findByIsActive(true)
                .orElse(null);

        String masterKey = config != null ? config.getMasterKey() : payDunyaProperties.getMasterKey();
        String privateKey = config != null ? config.getPrivateKey() : payDunyaProperties.getPrivateKey();
        String publicKey = config != null ? config.getPublicKey() : payDunyaProperties.getPublicKey();
        String token = config != null ? config.getToken() : payDunyaProperties.getToken();
        String mode = config != null ? config.getMode() : payDunyaProperties.getMode();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("PAYDUNYA-MASTER-KEY", masterKey);
        headers.set("PAYDUNYA-PRIVATE-KEY", privateKey);
        headers.set("PAYDUNYA-PUBLIC-KEY", publicKey);
        headers.set("PAYDUNYA-TOKEN", token);

        if ("live".equalsIgnoreCase(mode)) {
            headers.set("PAYDUNYA-MODE", "live");
        } else {
            headers.set("PAYDUNYA-MODE", "test");
        }

        return headers;
    }

    /**
     * Build checkout request body
     */
    private Map<String, Object> buildCheckoutBody(BigDecimal montant, String description, String reference) {
        Map<String, Object> body = new HashMap<>();

        // Invoice data
        Map<String, Object> invoice = new HashMap<>();
        invoice.put("total_amount", montant);
        invoice.put("description", description);

        // Custom data for tracking
        Map<String, Object> customData = new HashMap<>();
        customData.put("internal_reference", reference);
        customData.put("transaction_reference", reference);

        body.put("invoice", invoice);
        body.put("custom_data", customData);
        body.put("store", Map.of(
                "name", "Serenity JMS",
                "website_url", "https://serenity.bysmo.com"
        ));

        // Set IPN URL for callbacks
        String ipnUrl = payDunyaProperties.getIpnUrl();
        if (ipnUrl != null && !ipnUrl.isEmpty()) {
            body.put("actions", Map.of(
                    "callback_url", ipnUrl,
                    "return_url", "",
                    "cancel_url", ""
            ));
        }

        return body;
    }

    /**
     * Build disbursement request body
     */
    private Map<String, Object> buildDisbursementBody(String telephone, BigDecimal montant, String withdrawMode, String reference) {
        Map<String, Object> body = new HashMap<>();

        Map<String, Object> invoice = new HashMap<>();
        invoice.put("total_amount", montant);
        invoice.put("description", "Disbursement " + reference);

        Map<String, Object> customData = new HashMap<>();
        customData.put("internal_reference", reference);
        customData.put("withdraw_mode", withdrawMode);

        body.put("invoice", invoice);
        body.put("custom_data", customData);
        body.put("disburse", Map.of(
                "account_alias", telephone,
                "withdraw_mode", withdrawMode
        ));

        return body;
    }

    /**
     * Generate a unique transaction reference
     */
    private String generateReference(String prefix) {
        return prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() + "-" + System.currentTimeMillis() % 10000;
    }
}
