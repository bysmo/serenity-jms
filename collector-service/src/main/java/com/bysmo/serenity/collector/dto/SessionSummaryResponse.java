package com.bysmo.serenity.collector.dto;

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
public class SessionSummaryResponse {

    private UUID sessionId;
    private LocalDate dateSession;
    private BigDecimal montantOuverture;
    private BigDecimal montantFermeture;
    private BigDecimal totalCollectes;
    private long nombreCollectes;
    private BigDecimal ecart;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
}
