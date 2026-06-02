package com.serenity.epargne.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "epargne_versements")
public class EpargneVersement {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "souscription_id", nullable = false)
    private EpargneSouscription souscription;

    @Column(name = "souscription_id", insertable = false, updatable = false)
    private UUID souscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "echeance_id")
    private EpargneEcheance echeance;

    @Column(name = "echeance_id", insertable = false, updatable = false)
    private UUID echeanceId;

    @Column(name = "montant", nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(name = "date_versement", nullable = false)
    private LocalDateTime dateVersement;

    @Column(name = "mode_paiement", length = 20)
    private String modePaiement;

    @Column(name = "reference", length = 100)
    private String reference;

    @Column(name = "collecteur_id")
    private UUID collecteurId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public EpargneVersement() {
        this.dateVersement = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public EpargneSouscription getSouscription() {
        return souscription;
    }

    public void setSouscription(EpargneSouscription souscription) {
        this.souscription = souscription;
    }

    public UUID getSouscriptionId() {
        return souscriptionId;
    }

    public void setSouscriptionId(UUID souscriptionId) {
        this.souscriptionId = souscriptionId;
    }

    public EpargneEcheance getEcheance() {
        return echeance;
    }

    public void setEcheance(EpargneEcheance echeance) {
        this.echeance = echeance;
    }

    public UUID getEcheanceId() {
        return echeanceId;
    }

    public void setEcheanceId(UUID echeanceId) {
        this.echeanceId = echeanceId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateVersement() {
        return dateVersement;
    }

    public void setDateVersement(LocalDateTime dateVersement) {
        this.dateVersement = dateVersement;
    }

    public String getModePaiement() {
        return modePaiement;
    }

    public void setModePaiement(String modePaiement) {
        this.modePaiement = modePaiement;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public UUID getCollecteurId() {
        return collecteurId;
    }

    public void setCollecteurId(UUID collecteurId) {
        this.collecteurId = collecteurId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
