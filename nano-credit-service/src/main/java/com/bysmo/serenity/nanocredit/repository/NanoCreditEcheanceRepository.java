package com.bysmo.serenity.nanocredit.repository;

import com.bysmo.serenity.nanocredit.entity.NanoCreditEcheance;
import com.bysmo.serenity.nanocredit.entity.enums.EcheanceStatut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface NanoCreditEcheanceRepository extends JpaRepository<NanoCreditEcheance, UUID> {

    List<NanoCreditEcheance> findByNanoCreditIdOrderByNumeroEcheance(UUID nanoCreditId);

    List<NanoCreditEcheance> findByNanoCreditIdAndStatut(UUID nanoCreditId, EcheanceStatut statut);

    List<NanoCreditEcheance> findByStatutAndDateEcheanceBefore(EcheanceStatut statut, LocalDate date);

    List<NanoCreditEcheance> findByStatut(EcheanceStatut statut);

    long countByNanoCreditIdAndStatut(UUID nanoCreditId, EcheanceStatut statut);
}
