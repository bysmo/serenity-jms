package com.bysmo.serenity.payment.dto;

import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
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
public class PaymentMethodResponse {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private PaymentGateway gateway;
    private Boolean isActive;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private BigDecimal feesPercentage;
    private BigDecimal feesFixed;
    private Map<String, Object> config;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
