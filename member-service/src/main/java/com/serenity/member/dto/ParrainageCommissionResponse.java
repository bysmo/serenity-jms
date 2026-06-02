package com.serenity.member.dto;

import com.serenity.member.entity.enums.CommissionStatut;
import com.serenity.member.entity.enums.DeclencheurParrainage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParrainageCommissionResponse {

    private UUID id;
    private UUID parrainId;
    private UUID filleulId;
    private UUID configId;
    private Integer niveau;
    private DeclencheurParrainage declencheur;
    private BigDecimal montant;
    private CommissionStatut statut;
    private LocalDateTime disponibleLe;
    private LocalDateTime reclameLe;
    private LocalDateTime payeLe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
