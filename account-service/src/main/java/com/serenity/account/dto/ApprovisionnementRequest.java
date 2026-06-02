package com.serenity.account.dto;

import jakarta.validation.constraints.NotBlank;
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
public class ApprovisionnementRequest {

    @NotNull(message = "La caisse est obligatoire")
    private UUID caisseId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String motif;

    @NotBlank(message = "Le mode d'approvisionnement est obligatoire")
    private String modeApprovisionnement;

    private String referenceExterne;

    private UUID operateurId;
}
