package com.serenity.cotisation.dto;

import com.serenity.cotisation.enums.RemboursementStatut;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RemboursementResponse {

    private UUID id;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montant;
    private String motif;
    private RemboursementStatut statut;
    private UUID traitePar;
    private LocalDateTime dateTraitement;
    private String commentaire;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
