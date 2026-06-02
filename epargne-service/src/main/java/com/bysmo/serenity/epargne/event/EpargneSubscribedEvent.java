package com.bysmo.serenity.epargne.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpargneSubscribedEvent {

    private UUID souscriptionId;
    private UUID membreId;
    private UUID planId;
    private String planNom;
    private BigDecimal montant;
    private String frequence;
    private LocalDateTime dateSouscription;
    private UUID caisseId;
}
