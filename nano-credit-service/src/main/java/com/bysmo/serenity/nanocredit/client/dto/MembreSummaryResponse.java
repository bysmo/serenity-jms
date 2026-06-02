package com.bysmo.serenity.nanocredit.client.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembreSummaryResponse {

    private UUID id;
    private String numero;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private UUID segmentId;
    private String statut;
}
