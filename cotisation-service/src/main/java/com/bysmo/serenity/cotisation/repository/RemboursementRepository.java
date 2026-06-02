package com.bysmo.serenity.cotisation.repository;

import com.bysmo.serenity.cotisation.entity.Remboursement;
import com.bysmo.serenity.cotisation.enums.RemboursementStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RemboursementRepository extends JpaRepository<Remboursement, UUID> {

    List<Remboursement> findByMembreId(UUID membreId);

    List<Remboursement> findByCotisationId(UUID cotisationId);

    List<Remboursement> findByStatut(RemboursementStatut statut);

    List<Remboursement> findByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);
}
