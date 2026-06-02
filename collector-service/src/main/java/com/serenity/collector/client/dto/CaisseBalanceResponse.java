package com.serenity.collector.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseBalanceResponse {

    private UUID caisseId;
    private String numero;
    private BigDecimal soldeActuel;
}
