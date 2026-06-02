package com.serenity.collector.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollecteResponse {

    private UUID id;
    private UUID collecteSessionId;
    private UUID membreId;
    private String typeCollecte;
    private BigDecimal montant;
    private String echeanceType;
    private UUID echeanceId;
    private Boolean isConfirmed;
    private LocalDateTime confirmedAt;
    private String referenceTransaction;
    private LocalDateTime createdAt;
}
