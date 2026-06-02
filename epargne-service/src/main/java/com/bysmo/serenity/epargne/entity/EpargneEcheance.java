package com.bysmo.serenity.epargne.entity;

import com.bysmo.serenity.epargne.enums.EcheanceStatut;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "epargne_echeances")
public class EpargneEcheance {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "souscription_id", nullable = false)
    private EpargneSouscription souscription;

    @Column(name = "souscription_id", insertable = false, updatable = false)
    private UUID souscriptionId;

    @Column(name = "numero_echeance", nullable = false)
    private Integer numeroEcheance;

    @Column(name = "montant", nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private EcheanceStatut statut = EcheanceStatut.EN_ATTENTE;

    @Column(name = "date_paiement")
    private LocalDateTime datePaiement;

    @Column(name = "montant_paye", precision = 19, scale = 4)
    private BigDecimal montantPaye = BigDecimal.ZERO;

    @Column(name = "montant_penalite", precision = 19, scale = 4)
    private BigDecimal montantPenalite = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public EpargneEcheance() {
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

    public Integer getNumeroEcheance() {
        return numeroEcheance;
    }

    public void setNumeroEcheance(Integer numeroEcheance) {
        this.numeroEcheance = numeroEcheance;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDate getDateEcheance() {
        return dateEcheance;
    }

    public void setDateEcheance(LocalDate dateEcheance) {
        this.dateEcheance = dateEcheance;
    }

    public EcheanceStatut getStatut() {
        return statut;
    }

    public void setStatut(EcheanceStatut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDateTime datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontantPaye() {
        return montantPaye;
    }

    public void setMontantPaye(BigDecimal montantPaye) {
        this.montantPaye = montantPaye;
    }

    public BigDecimal getMontantPenalite() {
        return montantPenalite;
    }

    public void setMontantPenalite(BigDecimal montantPenalite) {
        this.montantPenalite = montantPenalite;
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
