package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.CompteExterneType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompteExterneResponse {

    private UUID id;
    private UUID membreId;
    private CompteExterneType type;
    private String identifiant;
    private String libelle;
    private String fournisseur;
    private Boolean isDefault;
    private Boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
