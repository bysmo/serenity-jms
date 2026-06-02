package com.bysmo.serenity.nanocredit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditEcheanceResponse {

    private UUID id;
    private UUID nanoCreditId;
    private Integer numeroEcheance;
    private BigDecimal montant;
    private BigDecimal montantPenalite;
    private LocalDate dateEcheance;
    private String statut;
    private LocalDateTime datePaiement;
    private BigDecimal montantPaye;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
