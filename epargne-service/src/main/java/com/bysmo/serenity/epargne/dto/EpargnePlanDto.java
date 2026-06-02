package com.bysmo.serenity.epargne.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EpargnePlanDto {

    private UUID id;
    private String nom;
    private String description;
    private BigDecimal montantMin;
    private BigDecimal montantMax;
    private String frequence;
    private BigDecimal tauxRemuneration;
    private Integer dureeMois;
    private UUID caisseId;
    private LocalTime heureLimitePaiement;
    private Integer delaiRappelHeures;
    private Integer intervalleRappelMinutes;
    private Boolean actif;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "Le nom du plan est obligatoire")
        private String nom;
        private String description;
        private BigDecimal montantMin;
        private BigDecimal montantMax;
        @NotBlank(message = "La fréquence est obligatoire")
        private String frequence;
        private BigDecimal tauxRemuneration;
        private Integer dureeMois;
        private LocalTime heureLimitePaiement;
        private Integer delaiRappelHeures;
        private Integer intervalleRappelMinutes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String nom;
        private String description;
        private BigDecimal montantMin;
        private BigDecimal montantMax;
        private String frequence;
        private BigDecimal tauxRemuneration;
        private Integer dureeMois;
        private LocalTime heureLimitePaiement;
        private Integer delaiRappelHeures;
        private Integer intervalleRappelMinutes;
        private String checksum;
    }
}
