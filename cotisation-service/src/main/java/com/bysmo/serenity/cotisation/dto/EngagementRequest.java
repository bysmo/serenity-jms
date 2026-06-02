package com.bysmo.serenity.cotisation.dto;

import com.bysmo.serenity.cotisation.enums.Frequence;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EngagementRequest {

    @NotNull(message = "La cotisation est obligatoire")
    private UUID cotisationId;

    @NotNull(message = "Le membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "Le montant engagé est obligatoire")
    private BigDecimal montantEngage;

    @NotNull(message = "La périodicité est obligatoire")
    private Frequence periodicite;

    @NotNull(message = "La période de début est obligatoire")
    private LocalDate periodeDebut;

    @NotNull(message = "La période de fin est obligatoire")
    private LocalDate periodeFin;

    private String tag;
}
