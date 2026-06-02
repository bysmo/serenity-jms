package com.serenity.cotisation.dto;

import com.serenity.cotisation.enums.AdhesionStatut;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdhesionResponse {

    private UUID id;
    private UUID cotisationId;
    private UUID membreId;
    private AdhesionStatut statut;
    private UUID traitePar;
    private LocalDateTime dateTraitement;
    private String motifRefus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
