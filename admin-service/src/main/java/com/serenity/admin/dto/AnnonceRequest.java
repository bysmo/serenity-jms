package com.serenity.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceRequest {

    @NotBlank(message = "Title is required")
    private String titre;

    private String contenu;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private String statut;

    private String type;

    private Integer ordre;

    private String segment;
}
