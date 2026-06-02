package com.bysmo.serenity.cotisation.repository;

import com.bysmo.serenity.cotisation.entity.Paiement;
import com.bysmo.serenity.cotisation.enums.PaiementStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaiementRepository extends JpaRepository<Paiement, UUID> {

    List<Paiement> findByMembreId(UUID membreId);

    List<Paiement> findByCotisationId(UUID cotisationId);

    List<Paiement> findByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);

    List<Paiement> findByStatut(PaiementStatut statut);

    List<Paiement> findByCotisationIdAndStatut(UUID cotisationId, PaiementStatut statut);
}
