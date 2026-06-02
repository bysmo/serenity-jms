package com.serenity.epargne.repository;

import com.serenity.epargne.entity.EpargneEcheance;
import com.serenity.epargne.enums.EcheanceStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EpargneEcheanceRepository extends JpaRepository<EpargneEcheance, UUID> {

    List<EpargneEcheance> findBySouscriptionIdOrderByNumeroEcheanceAsc(UUID souscriptionId);

    List<EpargneEcheance> findBySouscriptionIdAndStatut(UUID souscriptionId, EcheanceStatut statut);

    List<EpargneEcheance> findByStatutAndDateEcheanceBefore(EcheanceStatut statut, LocalDate date);

    List<EpargneEcheance> findByStatutAndDateEcheanceBetween(EcheanceStatut statut, LocalDate startDate, LocalDate endDate);

    @Query("SELECT e FROM EpargneEcheance e " +
           "JOIN EpargneSouscription s ON e.souscription.id = s.id " +
           "WHERE s.membreId = :membreId " +
           "AND e.dateEcheance BETWEEN :startDate AND :endDate " +
           "AND e.statut IN :statuts " +
           "ORDER BY e.dateEcheance ASC")
    List<EpargneEcheance> findUpcomingEcheancesByMembre(
            @Param("membreId") UUID membreId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuts") List<EcheanceStatut> statuts);

    @Query("SELECT e FROM EpargneEcheance e " +
           "WHERE e.dateEcheance BETWEEN :startDate AND :endDate " +
           "AND e.statut IN :statuts " +
           "ORDER BY e.dateEcheance ASC")
    List<EpargneEcheance> findEcheancesDueBetween(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("statuts") List<EcheanceStatut> statuts);

    @Query("SELECT e FROM EpargneEcheance e " +
           "WHERE e.statut = :statut " +
           "AND e.dateEcheance < :today " +
           "ORDER BY e.dateEcheance ASC")
    List<EpargneEcheance> findOverdueEcheances(
            @Param("statut") EcheanceStatut statut,
            @Param("today") LocalDate today);
}
