package com.bysmo.serenity.epargne.entity;

import com.bysmo.serenity.epargne.enums.EpargneFrequence;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "epargne_plans")
public class EpargnePlan {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "montant_min", precision = 19, scale = 4)
    private BigDecimal montantMin;

    @Column(name = "montant_max", precision = 19, scale = 4)
    private BigDecimal montantMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "frequence", length = 20)
    private EpargneFrequence frequence;

    @Column(name = "taux_remuneration", precision = 5, scale = 4)
    private BigDecimal tauxRemuneration;

    @Column(name = "duree_mois")
    private Integer dureeMois;

    @Column(name = "caisse_id")
    private UUID caisseId;

    @Column(name = "heure_limite_paiement")
    private LocalTime heureLimitePaiement;

    @Column(name = "delai_rappel_heures")
    private Integer delaiRappelHeures;

    @Column(name = "intervalle_rappel_minutes")
    private Integer intervalleRappelMinutes;

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "checksum", length = 64)
    private String checksum;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public EpargnePlan() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getMontantMin() {
        return montantMin;
    }

    public void setMontantMin(BigDecimal montantMin) {
        this.montantMin = montantMin;
    }

    public BigDecimal getMontantMax() {
        return montantMax;
    }

    public void setMontantMax(BigDecimal montantMax) {
        this.montantMax = montantMax;
    }

    public EpargneFrequence getFrequence() {
        return frequence;
    }

    public void setFrequence(EpargneFrequence frequence) {
        this.frequence = frequence;
    }

    public BigDecimal getTauxRemuneration() {
        return tauxRemuneration;
    }

    public void setTauxRemuneration(BigDecimal tauxRemuneration) {
        this.tauxRemuneration = tauxRemuneration;
    }

    public Integer getDureeMois() {
        return dureeMois;
    }

    public void setDureeMois(Integer dureeMois) {
        this.dureeMois = dureeMois;
    }

    public UUID getCaisseId() {
        return caisseId;
    }

    public void setCaisseId(UUID caisseId) {
        this.caisseId = caisseId;
    }

    public LocalTime getHeureLimitePaiement() {
        return heureLimitePaiement;
    }

    public void setHeureLimitePaiement(LocalTime heureLimitePaiement) {
        this.heureLimitePaiement = heureLimitePaiement;
    }

    public Integer getDelaiRappelHeures() {
        return delaiRappelHeures;
    }

    public void setDelaiRappelHeures(Integer delaiRappelHeures) {
        this.delaiRappelHeures = delaiRappelHeures;
    }

    public Integer getIntervalleRappelMinutes() {
        return intervalleRappelMinutes;
    }

    public void setIntervalleRappelMinutes(Integer intervalleRappelMinutes) {
        this.intervalleRappelMinutes = intervalleRappelMinutes;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
