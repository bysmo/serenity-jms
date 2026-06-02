package com.serenity.cotisation.repository;

import com.serenity.cotisation.entity.Cotisation;
import com.serenity.cotisation.enums.CotisationType;
import com.serenity.cotisation.enums.Visibilite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CotisationRepository extends JpaRepository<Cotisation, UUID>, JpaSpecificationExecutor<Cotisation> {

    List<Cotisation> findByActif(Boolean actif);

    List<Cotisation> findByType(CotisationType type);

    List<Cotisation> findByVisibilite(Visibilite visibilite);

    List<Cotisation> findByActifAndType(Boolean actif, CotisationType type);

    List<Cotisation> findByActifAndVisibilite(Boolean actif, Visibilite visibilite);

    List<Cotisation> findByCaisseId(UUID caisseId);

    List<Cotisation> findByCreatedByMembreId(UUID membreId);

    List<Cotisation> findByAdminMembreId(UUID membreId);
}
