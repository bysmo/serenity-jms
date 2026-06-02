package com.serenity.nanocredit.dto;

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
public class NanoCreditPalierRequest {

    @NotBlank(message = "Le nom du palier est obligatoire")
    private String nom;

    @NotNull(message = "Le montant plafond est obligatoire")
    @Positive(message = "Le montant plafond doit être positif")
    private BigDecimal montantPlafond;

    @NotNull(message = "La durée en jours est obligatoire")
    @Positive(message = "La durée doit être positive")
    private Integer dureeJours;

    @NotBlank(message = "La fréquence de remboursement est obligatoire")
    private String frequenceRemboursement;

    @NotNull(message = "Le taux d'intérêt est obligatoire")
    @Positive(message = "Le taux d'intérêt doit être positif")
    private BigDecimal tauxInteret;

    @NotNull(message = "La pénalité par jour est obligatoire")
    @Positive(message = "La pénalité par jour doit être positive")
    private BigDecimal penaliteParJour;

    private BigDecimal minMontantTotalRembourse;

    private BigDecimal minEpargneCumulee;

    private BigDecimal minEpargnePercent;

    private Integer minGarantQualite;

    private BigDecimal pourcentagePartageGarant;

    @Builder.Default
    private Boolean actif = true;
}
