package com.bysmo.serenity.nanocredit.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseRequest {

    private String nom;
    private String type;
    private String statut;
    @Builder.Default
    private BigDecimal soldeInitial = BigDecimal.ZERO;
    private UUID membreId;
    private UUID userId;
    private String numeroCoreBanking;
    @Builder.Default
    private BigDecimal seuilAlerte = BigDecimal.ZERO;
    private Map<String, Object> details;
}
