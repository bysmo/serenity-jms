package com.serenity.epargne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpargneEcheanceDto {

    private UUID id;
    private UUID souscriptionId;
    private Integer numeroEcheance;
    private BigDecimal montant;
    private LocalDate dateEcheance;
    private String statut;
    private LocalDateTime datePaiement;
    private BigDecimal montantPaye;
    private BigDecimal montantPenalite;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
