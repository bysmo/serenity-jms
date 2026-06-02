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
public class NanoCreditResponse {

    private UUID id;
    private UUID membreId;
    private UUID palierId;
    private String palierNom;
    private BigDecimal montant;
    private String statut;
    private String withdrawMode;
    private BigDecimal scoreAi;
    private BigDecimal scoreHumain;
    private BigDecimal scoreGlobal;
    private UUID compteRemboursementId;
    private UUID compteCreditId;
    private UUID compteImpayeId;
    private LocalDateTime dateOctroi;
    private LocalDate dateFinRemboursement;
    private BigDecimal montantPenalite;
    private Integer joursRetard;
    private LocalDateTime dateDernierCalculPenalite;
    private UUID createdBy;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
