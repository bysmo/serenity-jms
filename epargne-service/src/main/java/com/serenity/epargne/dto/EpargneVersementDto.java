package com.serenity.epargne.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpargneVersementDto {

    private UUID id;
    private UUID souscriptionId;
    private UUID echeanceId;
    private BigDecimal montant;
    private LocalDateTime dateVersement;
    private String modePaiement;
    private String reference;
    private UUID collecteurId;
    private LocalDateTime createdAt;
}
