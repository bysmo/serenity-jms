package com.bysmo.serenity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceResponse {

    private UUID id;
    private String titre;
    private String contenu;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private String statut;
    private String type;
    private Integer ordre;
    private String segment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
