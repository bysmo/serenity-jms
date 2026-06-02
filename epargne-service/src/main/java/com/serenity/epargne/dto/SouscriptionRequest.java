package com.serenity.epargne.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SouscriptionRequest {

    @NotNull(message = "L'identifiant du membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "L'identifiant du plan est obligatoire")
    private UUID planId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;
}
