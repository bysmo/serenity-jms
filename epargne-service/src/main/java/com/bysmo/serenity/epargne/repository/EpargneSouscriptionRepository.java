package com.bysmo.serenity.epargne.repository;

import com.bysmo.serenity.epargne.entity.EpargneSouscription;
import com.bysmo.serenity.epargne.enums.SouscriptionStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EpargneSouscriptionRepository extends JpaRepository<EpargneSouscription, UUID> {

    List<EpargneSouscription> findByMembreId(UUID membreId);

    List<EpargneSouscription> findByPlanId(UUID planId);

    List<EpargneSouscription> findByMembreIdAndStatut(UUID membreId, SouscriptionStatut statut);

    List<EpargneSouscription> findByStatut(SouscriptionStatut statut);

    List<EpargneSouscription> findByPlanIdAndStatut(UUID planId, SouscriptionStatut statut);
}
