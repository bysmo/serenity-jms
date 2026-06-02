package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.Membre;
import com.bysmo.serenity.member.entity.enums.MembreStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembreRepository extends JpaRepository<Membre, UUID> {

    Optional<Membre> findByNumero(String numero);

    Optional<Membre> findByTelephone(String telephone);

    Optional<Membre> findByEmail(String email);

    Optional<Membre> findByCodeParrainage(String codeParrainage);

    List<Membre> findBySegmentId(UUID segmentId);

    List<Membre> findByParrainId(UUID parrainId);

    List<Membre> findByStatut(MembreStatut statut);

    long countByNumeroStartingWith(String prefix);

    Optional<Membre> findTopByOrderByNumeroDesc();
}
