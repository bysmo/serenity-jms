package com.bysmo.serenity.collector.repository;

import com.bysmo.serenity.collector.entity.Collecte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface CollecteRepository extends JpaRepository<Collecte, UUID> {

    List<Collecte> findByCollecteSessionIdOrderByCreatedAtDesc(UUID sessionId);

    @Query("SELECT COALESCE(SUM(c.montant), 0) FROM Collecte c WHERE c.collecteSessionId = :sessionId AND c.isConfirmed = true")
    BigDecimal sumConfirmedBySessionId(@Param("sessionId") UUID sessionId);

    long countByCollecteSessionId(UUID sessionId);
}
