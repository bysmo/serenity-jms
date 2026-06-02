package com.bysmo.serenity.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Entity
@Table(name = "annonces")
public class Annonce {

    @jakarta.persistence.Id
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "titre", nullable = false)
    private String titre;

    @Column(name = "contenu", columnDefinition = "TEXT")
    private String contenu;

    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @Column(name = "date_fin")
    private LocalDate dateFin;

    @Column(name = "statut", length = 20)
    @Builder.Default
    private String statut = "active";

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "ordre")
    @Builder.Default
    private Integer ordre = 0;

    @Column(name = "segment", length = 100)
    private String segment;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
