package com.bysmo.serenity.cotisation.dto;

import com.bysmo.serenity.cotisation.enums.ModePaiement;
import com.bysmo.serenity.cotisation.enums.PaiementStatut;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementResponse {

    private UUID id;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montant;
    private ModePaiement modePaiement;
    private PaiementStatut statut;
    private String reference;
    private UUID walletAliasId;
    private UUID compteExterneId;
    private Map<String, Object> metadata;
    private LocalDateTime datePaiement;
    private UUID traitePar;
    private LocalDateTime dateTraitement;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
