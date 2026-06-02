package com.bysmo.serenity.account.repository;

import com.bysmo.serenity.account.entity.Transfert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransfertRepository extends JpaRepository<Transfert, UUID> {

    List<Transfert> findByCaisseSourceId(UUID caisseSourceId);

    List<Transfert> findByCaisseDestinationId(UUID caisseDestinationId);

    List<Transfert> findByCaisseSourceIdOrCaisseDestinationId(UUID caisseSourceId, UUID caisseDestinationId);

    List<Transfert> findByStatut(String statut);
}
