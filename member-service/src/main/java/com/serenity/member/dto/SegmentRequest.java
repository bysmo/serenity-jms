package com.serenity.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SegmentRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100)
    private String nom;

    @NotBlank(message = "Le slug est obligatoire")
    @Size(max = 100)
    private String slug;

    private String description;

    @Size(max = 20)
    private String couleur;

    @Size(max = 50)
    private String icone;

    @Builder.Default
    private Boolean isDefault = false;

    @Builder.Default
    private Boolean actif = true;
}
