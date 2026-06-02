package com.serenity.cotisation.dto;

import com.serenity.cotisation.enums.EngagementStatut;
import com.serenity.cotisation.enums.Frequence;
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
public class EngagementResponse {

    private UUID id;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montantEngage;
    private BigDecimal montantPaye;
    private Frequence periodicite;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private EngagementStatut statut;
    private String tag;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
