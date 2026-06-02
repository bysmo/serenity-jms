package com.bysmo.serenity.account.entity;

import com.bysmo.serenity.account.entity.enums.CaisseStatut;
import com.bysmo.serenity.account.entity.enums.CaisseType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.types.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "caisses")
public class Caisse {

    @Id
    private UUID id;

    @Column(name = "numero", nullable = false, unique = true, length = 30)
    private String numero;

    @Column(name = "nom", nullable = false, length = 200)
    private String nom;

    @Column(name = "type", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private CaisseType type;

    @Column(name = "statut", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CaisseStatut statut = CaisseStatut.ACTIVE;

    @Column(name = "solde_initial", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(name = "membre_id")
    private UUID membreId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "numero_core_banking", length = 50)
    private String numeroCoreBanking;

    @Column(name = "seuil_alerte", nullable = false, precision = 19, scale = 2)
    @Builder.Default
    private BigDecimal seuilAlerte = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.JSONB)
    @Column(name = "details", columnDefinition = "jsonb")
    private Map<String, Object> details;

    @Column(name = "checksum", length = 64)
    private String checksum;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Caisse other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
