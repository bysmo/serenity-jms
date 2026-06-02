package com.serenity.cotisation.repository;

import com.serenity.cotisation.entity.Engagement;
import com.serenity.cotisation.enums.EngagementStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EngagementRepository extends JpaRepository<Engagement, UUID> {

    List<Engagement> findByMembreId(UUID membreId);

    List<Engagement> findByCotisationId(UUID cotisationId);

    List<Engagement> findByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);

    List<Engagement> findByStatut(EngagementStatut statut);

    List<Engagement> findByStatutAndPeriodeFinBefore(EngagementStatut statut, LocalDate date);

    List<Engagement> findByStatutAndPeriodeFinBetween(EngagementStatut statut, LocalDate startDate, LocalDate endDate);

    List<Engagement> findByStatutAndPeriodeDebutBetween(EngagementStatut statut, LocalDate startDate, LocalDate endDate);
}
