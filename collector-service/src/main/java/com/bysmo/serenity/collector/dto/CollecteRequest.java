package com.bysmo.serenity.collector.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollecteRequest {

    @NotNull(message = "L'identifiant de la session est obligatoire")
    private UUID collecteSessionId;

    @NotNull(message = "L'identifiant du membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "Le type de collecte est obligatoire")
    private String typeCollecte;

    @NotNull(message = "Le montant est obligatoire")
    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String echeanceType;
    private UUID echeanceId;
}
