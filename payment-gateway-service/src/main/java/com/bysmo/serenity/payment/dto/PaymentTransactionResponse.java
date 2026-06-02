package com.bysmo.serenity.payment.dto;

import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import com.bysmo.serenity.payment.entity.enums.TransactionStatut;
import com.bysmo.serenity.payment.entity.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private PaymentGateway gateway;
    private TransactionType transactionType;
    private TransactionStatut statut;
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
