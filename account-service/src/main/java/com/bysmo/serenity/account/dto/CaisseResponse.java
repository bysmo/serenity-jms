package com.bysmo.serenity.account.dto;

import com.bysmo.serenity.account.entity.enums.CaisseStatut;
import com.bysmo.serenity.account.entity.enums.CaisseType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaisseResponse {

    private UUID id;
    private String numero;
    private String nom;
    private CaisseType type;
    private CaisseStatut statut;
    private BigDecimal soldeInitial;
    private UUID membreId;
    private UUID userId;
    private String numeroCoreBanking;
    private BigDecimal seuilAlerte;
    private Map<String, Object> details;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
