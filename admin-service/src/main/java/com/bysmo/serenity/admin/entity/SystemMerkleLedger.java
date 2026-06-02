package com.bysmo.serenity.admin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
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
@Table(name = "system_merkle_ledger")
public class SystemMerkleLedger {

    @jakarta.persistence.Id
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "table_name", nullable = false, length = 100)
    private String tableName;

    @Column(name = "record_id", nullable = false)
    private UUID recordId;

    @Column(name = "action", nullable = false, length = 20)
    private String action;

    @Column(name = "record_checksum", nullable = false, length = 64)
    private String recordChecksum;

    @Column(name = "hash_chain", nullable = false, length = 64)
    private String hashChain;

    @Column(name = "hmac_signature", length = 128)
    private String hmacSignature;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
