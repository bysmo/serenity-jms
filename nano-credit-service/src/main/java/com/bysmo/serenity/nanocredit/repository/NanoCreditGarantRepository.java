package com.bysmo.serenity.nanocredit.repository;

import com.bysmo.serenity.nanocredit.entity.NanoCreditGarant;
import com.bysmo.serenity.nanocredit.entity.enums.GarantStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NanoCreditGarantRepository extends JpaRepository<NanoCreditGarant, UUID> {

    List<NanoCreditGarant> findByNanoCreditId(UUID nanoCreditId);

    List<NanoCreditGarant> findByGarantMembreId(UUID garantMembreId);

    List<NanoCreditGarant> findByNanoCreditIdAndStatut(UUID nanoCreditId, GarantStatut statut);

    List<NanoCreditGarant> findByStatut(GarantStatut statut);

    long countByNanoCreditIdAndStatut(UUID nanoCreditId, GarantStatut statut);
}
