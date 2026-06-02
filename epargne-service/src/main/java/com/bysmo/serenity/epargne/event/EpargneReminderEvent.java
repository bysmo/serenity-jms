package com.bysmo.serenity.epargne.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpargneReminderEvent {

    private UUID echeanceId;
    private UUID souscriptionId;
    private UUID membreId;
    private UUID planId;
    private String planNom;
    private BigDecimal montantEcheance;
    private LocalDate dateEcheance;
    private String frequence;
    private BigDecimal montantPaye;
    private BigDecimal montantRestant;
}
