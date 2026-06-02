package com.serenity.epargne.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaisseResponse {

    private UUID id;
    private String nom;
    private String description;
    private BigDecimal solde;
    private String type;
    private Boolean actif;
    private LocalDateTime createdAt;
}
