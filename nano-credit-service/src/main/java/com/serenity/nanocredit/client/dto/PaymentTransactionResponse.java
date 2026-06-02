package com.serenity.nanocredit.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransactionResponse {

    private UUID id;
    private String reference;
    private String externalReference;
    private String gateway;
    private String transactionType;
    private String statut;
    private String telephone;
    private BigDecimal montant;
    private BigDecimal fees;
    private BigDecimal netAmount;
    private String currency;
    private String withdrawMode;
    private String description;
    private String internalReference;
    private Map<String, Object> gatewayResponse;
    private Map<String, Object> callbackData;
    private String errorMessage;
    private UUID organisationId;
    private UUID membreId;
    private UUID createdBy;
    private LocalDateTime confirmedAt;
    private LocalDateTime failedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
