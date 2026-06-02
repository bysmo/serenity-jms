package com.serenity.nanocredit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NanoCreditGarantResponse {

    private UUID id;
    private UUID nanoCreditId;
    private UUID garantMembreId;
    private String garantNom;
    private String garantPrenom;
    private Integer qualite;
    private BigDecimal soldeGarantie;
    private BigDecimal pourcentagePartage;
    private String statut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
