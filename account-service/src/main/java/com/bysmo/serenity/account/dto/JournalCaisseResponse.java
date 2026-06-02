package com.bysmo.serenity.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalCaisseResponse {

    private UUID caisseId;
    private String caisseNumero;
    private String caisseNom;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private BigDecimal soldeDebut;
    private List<MouvementCaisseResponse> mouvements;
    private BigDecimal totalEntrees;
    private BigDecimal totalSorties;
    private BigDecimal soldeFin;
}
