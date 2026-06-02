package com.serenity.account.repository;

import com.serenity.account.entity.SortieCaisse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SortieCaisseRepository extends JpaRepository<SortieCaisse, UUID> {

    List<SortieCaisse> findByCaisseId(UUID caisseId);

    List<SortieCaisse> findByTypeSortie(String typeSortie);

    List<SortieCaisse> findByCaisseIdOrderByCreatedAtDesc(UUID caisseId);
}
