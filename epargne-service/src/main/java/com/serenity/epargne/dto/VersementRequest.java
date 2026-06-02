package com.serenity.epargne.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersementRequest {

    @NotNull(message = "L'identifiant de la souscription est obligatoire")
    private UUID souscriptionId;

    private UUID echeanceId;

    @NotNull(message = "Le montant est obligatoire")
    private BigDecimal montant;

    private String modePaiement;

    private String reference;

    private UUID collecteurId;
}
