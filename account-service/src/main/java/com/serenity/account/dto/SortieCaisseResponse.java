package com.serenity.account.dto;

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
public class SortieCaisseResponse {

    private UUID id;
    private UUID caisseId;
    private BigDecimal montant;
    private String motif;
    private String typeSortie;
    private String beneficiaire;
    private String referenceExterne;
    private UUID operateurId;
    private LocalDateTime createdAt;
}
