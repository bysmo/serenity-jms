package com.bysmo.serenity.cotisation.dto;

import com.bysmo.serenity.cotisation.enums.ModePaiement;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementRequest {

    @NotNull(message = "La cotisation est obligatoire")
    private UUID cotisationId;

    @NotNull(message = "Le membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    private String reference;

    private UUID walletAliasId;

    private UUID compteExterneId;

    private Map<String, Object> metadata;
}
