package com.bysmo.serenity.account.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "sorties_caisse")
public class SortieCaisse {

    @Id
    private UUID id;

    @Column(name = "caisse_id", nullable = false)
    private UUID caisseId;

    @Column(name = "montant", nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "motif", nullable = false, columnDefinition = "TEXT")
    private String motif;

    @Column(name = "type_sortie", nullable = false, length = 30)
    private String typeSortie;

    @Column(name = "beneficiaire", length = 255)
    private String beneficiaire;

    @Column(name = "reference_externe", length = 100)
    private String referenceExterne;

    @Column(name = "operateur_id")
    private UUID operateurId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SortieCaisse other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
