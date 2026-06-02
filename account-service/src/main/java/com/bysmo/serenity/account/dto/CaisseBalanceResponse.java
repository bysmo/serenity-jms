package com.bysmo.serenity.account.dto;

import com.bysmo.serenity.account.entity.enums.CaisseStatut;
import com.bysmo.serenity.account.entity.enums.CaisseType;
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
public class CaisseBalanceResponse {

    private UUID id;
    private String numero;
    private String nom;
    private CaisseType type;
    private CaisseStatut statut;
    private BigDecimal soldeInitial;
    private BigDecimal totalEntrees;
    private BigDecimal totalSorties;
    private BigDecimal soldeActuel;
    private BigDecimal seuilAlerte;
    private boolean lowBalance;
    private LocalDateTime computedAt;
}
