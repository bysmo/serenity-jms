package com.bysmo.serenity.nanocredit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditPalierResponse {

    private UUID id;
    private String numero;
    private String nom;
    private BigDecimal montantPlafond;
    private Integer dureeJours;
    private String frequenceRemboursement;
    private BigDecimal tauxInteret;
    private BigDecimal penaliteParJour;
    private BigDecimal minMontantTotalRembourse;
    private BigDecimal minEpargneCumulee;
    private BigDecimal minEpargnePercent;
    private Integer minGarantQualite;
    private BigDecimal pourcentagePartageGarant;
    private Boolean actif;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
