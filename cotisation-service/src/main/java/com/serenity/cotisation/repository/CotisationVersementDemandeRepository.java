package com.serenity.cotisation.repository;

import com.serenity.cotisation.entity.CotisationVersementDemande;
import com.serenity.cotisation.enums.VersementDemandeStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CotisationVersementDemandeRepository extends JpaRepository<CotisationVersementDemande, UUID> {

    List<CotisationVersementDemande> findByMembreId(UUID membreId);

    List<CotisationVersementDemande> findByCotisationId(UUID cotisationId);

    List<CotisationVersementDemande> findByStatut(VersementDemandeStatut statut);

    List<CotisationVersementDemande> findByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);
}
