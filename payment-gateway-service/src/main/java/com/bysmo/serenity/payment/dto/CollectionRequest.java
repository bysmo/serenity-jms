package com.bysmo.serenity.payment.dto;

import com.bysmo.serenity.payment.entity.enums.PaymentGateway;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionRequest {

    @NotBlank(message = "Telephone is required")
    private String telephone;

    @NotNull(message = "Montant is required")
    @Positive(message = "Montant must be positive")
    private BigDecimal montant;

    @NotNull(message = "Gateway is required")
    private PaymentGateway gateway;

    private String internalReference;
}
