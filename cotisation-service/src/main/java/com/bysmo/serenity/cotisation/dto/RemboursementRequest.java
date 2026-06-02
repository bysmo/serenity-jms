package com.bysmo.serenity.cotisation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemboursementRequest {

    @NotNull(message = "La cotisation est obligatoire")
    private UUID cotisationId;

    @NotNull(message = "Le membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    private String motif;
}
