package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.MembreCompteExterne;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembreCompteExterneRepository extends JpaRepository<MembreCompteExterne, UUID> {

    List<MembreCompteExterne> findByMembreId(UUID membreId);

    Optional<MembreCompteExterne> findByIdentifiant(String identifiant);
}
