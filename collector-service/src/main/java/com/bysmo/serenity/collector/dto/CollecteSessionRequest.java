package com.bysmo.serenity.collector.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollecteSessionRequest {

    @NotNull(message = "L'identifiant utilisateur est obligatoire")
    private UUID userId;

    @NotNull(message = "Le montant d'ouverture est obligatoire")
    private BigDecimal montantOuverture;
}
