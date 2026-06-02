package com.bysmo.serenity.account.dto;

import com.bysmo.serenity.account.entity.enums.MouvementType;
import com.bysmo.serenity.account.entity.enums.Sens;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementCaisseRequest {

    @NotNull(message = "L'identifiant de la caisse est obligatoire")
    private UUID caisseId;

    @NotNull(message = "Le type de mouvement est obligatoire")
    private MouvementType type;

    @NotNull(message = "Le sens du mouvement est obligatoire")
    private Sens sens;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String description;

    private String referenceType;

    private UUID referenceId;

    private UUID operateurId;
}
