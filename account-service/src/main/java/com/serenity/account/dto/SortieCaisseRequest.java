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
public class SortieCaisseRequest {

    @NotNull(message = "La caisse est obligatoire")
    private UUID caisseId;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    @NotBlank(message = "Le motif est obligatoire")
    private String motif;

    @NotBlank(message = "Le type de sortie est obligatoire")
    private String typeSortie;

    private String beneficiaire;

    private String referenceExterne;

    private UUID operateurId;
}
