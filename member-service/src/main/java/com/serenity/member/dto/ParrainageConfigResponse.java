package com.serenity.member.dto;

import com.serenity.member.entity.enums.DeclencheurParrainage;
import com.serenity.member.entity.enums.TypeRemuneration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParrainageConfigResponse {

    private UUID id;
    private Boolean actif;
    private TypeRemuneration typeRemuneration;
    private BigDecimal montantFixe;
    private BigDecimal pourcentage;
    private DeclencheurParrainage declencheur;
    private Integer niveauMax;
    private Integer delaiDisponibiliteJours;
    private BigDecimal plafondMensuel;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
