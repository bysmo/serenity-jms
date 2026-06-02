package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.DeclencheurParrainage;
import com.bysmo.serenity.member.entity.enums.TypeRemuneration;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParrainageConfigRequest {

    @NotNull(message = "Le statut actif est obligatoire")
    private Boolean actif;

    @NotNull(message = "Le type de rémunération est obligatoire")
    private TypeRemuneration typeRemuneration;

    @Builder.Default
    private BigDecimal montantFixe = BigDecimal.ZERO;

    @Builder.Default
    private BigDecimal pourcentage = BigDecimal.ZERO;

    @NotNull(message = "Le déclencheur est obligatoire")
    private DeclencheurParrainage declencheur;

    @Builder.Default
    private Integer niveauMax = 1;

    @Builder.Default
    private Integer delaiDisponibiliteJours = 30;

    @Builder.Default
    private BigDecimal plafondMensuel = BigDecimal.ZERO;

    private String description;
}
