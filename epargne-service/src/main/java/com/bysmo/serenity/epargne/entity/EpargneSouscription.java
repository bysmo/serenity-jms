package com.bysmo.serenity.epargne.entity;

import com.bysmo.serenity.epargne.enums.SouscriptionStatut;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "epargne_souscriptions")
public class EpargneSouscription {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "membre_id", nullable = false)
    private UUID membreId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private EpargnePlan plan;

    @Column(name = "plan_id", insertable = false, updatable = false)
    private UUID planId;

    @Column(name = "montant", nullable = false, precision = 19, scale = 4)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private SouscriptionStatut statut = SouscriptionStatut.ACTIVE;

    @Column(name = "date_souscription", nullable = false)
    private LocalDateTime dateSouscription;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "caisse_id")
    private UUID caisseId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public EpargneSouscription() {
        this.dateSouscription = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMembreId() {
        return membreId;
    }

    public void setMembreId(UUID membreId) {
        this.membreId = membreId;
    }

    public EpargnePlan getPlan() {
        return plan;
    }

    public void setPlan(EpargnePlan plan) {
        this.plan = plan;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public SouscriptionStatut getStatut() {
        return statut;
    }

    public void setStatut(SouscriptionStatut statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateSouscription() {
        return dateSouscription;
    }

    public void setDateSouscription(LocalDateTime dateSouscription) {
        this.dateSouscription = dateSouscription;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public UUID getCaisseId() {
        return caisseId;
    }

    public void setCaisseId(UUID caisseId) {
        this.caisseId = caisseId;
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
