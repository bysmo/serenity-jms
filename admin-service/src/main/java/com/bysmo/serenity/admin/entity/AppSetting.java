package com.bysmo.serenity.admin.entity;

import com.bysmo.serenity.admin.entity.enums.SettingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "app_settings")
public class AppSetting {

    @jakarta.persistence.Id
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "cle", nullable = false, unique = true)
    private String cle;

    @Column(name = "valeur", columnDefinition = "TEXT")
    private String valeur;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SettingType type;

    @Column(name = "groupe")
    private String groupe;

    @Column(name = "checksum", length = 64)
    private String checksum;

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
