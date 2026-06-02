package com.serenity.collector.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollecteSessionResponse {

    private UUID id;
    private UUID userId;
    private LocalDate dateSession;
    private String statut;
    private BigDecimal montantOuverture;
    private BigDecimal montantFermeture;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
}
