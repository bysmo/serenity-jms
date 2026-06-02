package com.bysmo.serenity.account.dto;

import com.bysmo.serenity.account.entity.enums.MouvementType;
import com.bysmo.serenity.account.entity.enums.Sens;
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
public class MouvementCaisseResponse {

    private UUID id;
    private UUID caisseId;
    private MouvementType type;
    private Sens sens;
    private BigDecimal montant;
    private BigDecimal soldeAvant;
    private BigDecimal soldeApres;
    private LocalDateTime dateOperation;
    private String description;
    private String referenceType;
    private UUID referenceId;
    private UUID operateurId;
    private LocalDateTime createdAt;
}
