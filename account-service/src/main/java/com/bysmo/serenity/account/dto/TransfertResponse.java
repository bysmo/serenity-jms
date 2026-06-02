package com.bysmo.serenity.account.dto;

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
public class TransfertResponse {

    private UUID id;
    private UUID caisseSourceId;
    private UUID caisseDestinationId;
    private BigDecimal montant;
    private String motif;
    private String statut;
    private UUID operateurId;
    private LocalDateTime createdAt;
}
