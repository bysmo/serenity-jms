package com.serenity.account.repository;

import com.serenity.account.entity.MouvementCaisse;
import com.serenity.account.entity.enums.MouvementType;
import com.serenity.account.entity.enums.Sens;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MouvementCaisseRepository extends JpaRepository<MouvementCaisse, UUID> {

    List<MouvementCaisse> findByCaisseIdOrderByDateOperationDesc(UUID caisseId);

    List<MouvementCaisse> findByCaisseIdAndDateOperationBetween(UUID caisseId, LocalDateTime debut, LocalDateTime fin);

    List<MouvementCaisse> findByReferenceTypeAndReferenceId(String referenceType, UUID referenceId);

    List<MouvementCaisse> findByCaisseIdAndType(UUID caisseId, MouvementType type);

    List<MouvementCaisse> findByCaisseIdAndSens(UUID caisseId, Sens sens);

    List<MouvementCaisse> findByTypeAndSens(MouvementType type, Sens sens);

    List<MouvementCaisse> findByCaisseIdAndTypeAndDateOperationBetween(
            UUID caisseId, MouvementType type, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MouvementCaisse m WHERE m.caisseId = :caisseId AND m.sens = :sens")
    BigDecimal sumByCaisseIdAndSens(@Param("caisseId") UUID caisseId, @Param("sens") Sens sens);

    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MouvementCaisse m WHERE m.caisseId = :caisseId AND m.sens = :sens AND m.dateOperation BETWEEN :debut AND :fin")
    BigDecimal sumByCaisseIdAndSensAndDateOperationBetween(
            @Param("caisseId") UUID caisseId,
            @Param("sens") Sens sens,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MouvementCaisse m WHERE m.sens = :sens")
    BigDecimal sumAllBySens(@Param("sens") Sens sens);

    List<MouvementCaisse> findByCaisseIdAndSensAndDateOperationBetween(
            UUID caisseId, Sens sens, LocalDateTime debut, LocalDateTime fin);

    List<MouvementCaisse> findByType(MouvementType type);

    @Query("SELECT m FROM MouvementCaisse m WHERE " +
            "(:caisseId IS NULL OR m.caisseId = :caisseId) AND " +
            "(:type IS NULL OR m.type = :type) AND " +
            "(:sens IS NULL OR m.sens = :sens) AND " +
            "(:debut IS NULL OR m.dateOperation >= :debut) AND " +
            "(:fin IS NULL OR m.dateOperation <= :fin) " +
            "ORDER BY m.dateOperation DESC")
    List<MouvementCaisse> findByFilters(
            @Param("caisseId") UUID caisseId,
            @Param("type") MouvementType type,
            @Param("sens") Sens sens,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);
}
