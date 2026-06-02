package com.serenity.collector.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OpenSessionRequest {

    @NotNull(message = "Le montant d'ouverture est obligatoire")
    @PositiveOrZero(message = "Le montant d'ouverture doit être positif ou zéro")
    private BigDecimal montantOuverture;
}
