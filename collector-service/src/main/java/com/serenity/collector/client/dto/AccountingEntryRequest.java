package com.serenity.collector.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountingEntryRequest {

    private UUID caisseId;
    private BigDecimal montant;
    private String sens;
    private String type;
    private String description;
    private String referenceType;
    private UUID referenceId;
}
