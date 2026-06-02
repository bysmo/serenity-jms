package com.bysmo.serenity.payment.dto;

import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;

    @Builder.Default
    private Boolean isActive = true;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private BigDecimal feesPercentage;

    private BigDecimal feesFixed;

    private Map<String, Object> config;
}
