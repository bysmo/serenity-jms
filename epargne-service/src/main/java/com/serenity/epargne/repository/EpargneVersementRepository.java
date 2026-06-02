package com.serenity.epargne.repository;

import com.serenity.epargne.entity.EpargneVersement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface EpargneVersementRepository extends JpaRepository<EpargneVersement, UUID> {

    List<EpargneVersement> findBySouscriptionIdOrderByDateVersementDesc(UUID souscriptionId);

    List<EpargneVersement> findByEcheanceIdOrderByDateVersementDesc(UUID echeanceId);

    @Query("SELECT COALESCE(SUM(v.montant), 0) FROM EpargneVersement v WHERE v.souscriptionId = :souscriptionId")
    BigDecimal sumMontantBySouscriptionId(@Param("souscriptionId") UUID souscriptionId);

    @Query("SELECT COALESCE(SUM(v.montant), 0) FROM EpargneVersement v WHERE v.echeanceId = :echeanceId")
    BigDecimal sumMontantByEcheanceId(@Param("echeanceId") UUID echeanceId);
}
