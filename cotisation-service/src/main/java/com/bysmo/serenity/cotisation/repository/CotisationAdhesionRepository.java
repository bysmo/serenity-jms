package com.bysmo.serenity.cotisation.repository;

import com.bysmo.serenity.cotisation.entity.CotisationAdhesion;
import com.bysmo.serenity.cotisation.enums.AdhesionStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CotisationAdhesionRepository extends JpaRepository<CotisationAdhesion, UUID> {

    List<CotisationAdhesion> findByMembreId(UUID membreId);

    List<CotisationAdhesion> findByCotisationId(UUID cotisationId);

    List<CotisationAdhesion> findByStatut(AdhesionStatut statut);

    Optional<CotisationAdhesion> findByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);

    boolean existsByMembreIdAndCotisationId(UUID membreId, UUID cotisationId);
}
