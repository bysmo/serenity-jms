package com.serenity.admin.repository;

import com.serenity.admin.entity.SystemMerkleLedger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SystemMerkleLedgerRepository extends JpaRepository<SystemMerkleLedger, UUID> {

    Optional<SystemMerkleLedger> findTopByTableNameOrderByCreatedAtDesc(String tableName);

    List<SystemMerkleLedger> findByTableNameAndRecordId(String tableName, UUID recordId);

    List<SystemMerkleLedger> findByTableNameOrderByCreatedAtAsc(String tableName);

    List<SystemMerkleLedger> findByTableNameOrderByCreatedAtDesc(String tableName);

    List<SystemMerkleLedger> findAllByOrderByCreatedAtAsc();
}
