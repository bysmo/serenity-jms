package com.bysmo.serenity.epargne.client.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountingEntryRequest {

    @NotNull
    private UUID caisseId;

    @NotBlank
    private String type;

    @NotNull
    private BigDecimal montant;

    private String description;

    private String reference;

    private UUID entiteId;

    private String entiteType;
}
