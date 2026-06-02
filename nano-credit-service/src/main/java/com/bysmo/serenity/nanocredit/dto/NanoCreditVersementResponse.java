package com.bysmo.serenity.nanocredit.dto;

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
public class NanoCreditVersementResponse {

    private UUID id;
    private UUID nanoCreditId;
    private UUID echeanceId;
    private BigDecimal montant;
    private LocalDateTime dateVersement;
    private String modePaiement;
    private String reference;
    private LocalDateTime createdAt;
}
