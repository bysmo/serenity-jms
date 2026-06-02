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
public class EpargneSouscriptionDto {

    private UUID id;
    private UUID membreId;
    private UUID planId;
    private String planNom;
    private String planFrequence;
    private BigDecimal montant;
    private String statut;
    private LocalDateTime dateSouscription;
    private LocalDate dateFin;
    private UUID caisseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
