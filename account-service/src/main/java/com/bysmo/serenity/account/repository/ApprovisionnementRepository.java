package com.bysmo.serenity.account.repository;

import com.bysmo.serenity.account.entity.Approvisionnement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ApprovisionnementRepository extends JpaRepository<Approvisionnement, UUID> {

    List<Approvisionnement> findByCaisseId(UUID caisseId);

    List<Approvisionnement> findByModeApprovisionnement(String modeApprovisionnement);

    List<Approvisionnement> findByCaisseIdOrderByCreatedAtDesc(UUID caisseId);
}
