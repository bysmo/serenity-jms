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
public class MouvementCaisseResponse {

    private UUID id;
    private UUID caisseId;
    private String type;
    private BigDecimal montant;
    private String description;
    private String reference;
    private LocalDateTime dateMouvement;
    private LocalDateTime createdAt;
}
