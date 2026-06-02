package com.bysmo.serenity.account.dto;

import com.bysmo.serenity.account.entity.enums.CaisseStatut;
import com.bysmo.serenity.account.entity.enums.CaisseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseRequest {

    @NotBlank(message = "Le nom de la caisse est obligatoire")
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String nom;

    @NotNull(message = "Le type de caisse est obligatoire")
    private CaisseType type;

    private CaisseStatut statut;

    @Builder.Default
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    private UUID membreId;

    private UUID userId;

    @Size(max = 50, message = "Le numéro core banking ne doit pas dépasser 50 caractères")
    private String numeroCoreBanking;

    @Builder.Default
    private BigDecimal seuilAlerte = BigDecimal.ZERO;

    private Map<String, Object> details;
}
