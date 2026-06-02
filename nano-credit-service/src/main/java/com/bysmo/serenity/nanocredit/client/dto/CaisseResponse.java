package com.bysmo.serenity.nanocredit.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseResponse {

    private UUID id;
    private String numero;
    private String nom;
    private String type;
    private String statut;
    private BigDecimal soldeInitial;
    private UUID membreId;
    private UUID userId;
    private String numeroCoreBanking;
    private BigDecimal seuilAlerte;
    private Map<String, Object> details;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
