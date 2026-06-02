package com.serenity.account.repository;

import com.serenity.account.entity.Caisse;
import com.serenity.account.entity.enums.CaisseStatut;
import com.serenity.account.entity.enums.CaisseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaisseRepository extends JpaRepository<Caisse, UUID> {

    Optional<Caisse> findByNumero(String numero);

    List<Caisse> findByMembreId(UUID membreId);

    List<Caisse> findByType(CaisseType type);

    List<Caisse> findByStatut(CaisseStatut statut);

    List<Caisse> findByTypeAndStatut(CaisseType type, CaisseStatut statut);

    List<Caisse> findByMembreIdAndType(UUID membreId, CaisseType type);

    boolean existsByNumero(String numero);

    boolean existsByMembreIdAndType(UUID membreId, CaisseType type);
}
