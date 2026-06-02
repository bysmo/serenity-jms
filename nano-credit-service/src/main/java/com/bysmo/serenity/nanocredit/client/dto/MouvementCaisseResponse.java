package com.bysmo.serenity.nanocredit.client.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MouvementCaisseResponse {

    private UUID id;
    private UUID caisseId;
    private String type;
    private String sens;
    private BigDecimal montant;
    private LocalDateTime dateOperation;
}
