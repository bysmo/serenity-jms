package com.bysmo.serenity.account.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransfertRequest {

    @NotNull(message = "La caisse source est obligatoire")
    private UUID caisseSourceId;

    @NotNull(message = "La caisse destination est obligatoire")
    private UUID caisseDestinationId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String motif;

    private UUID operateurId;
}
