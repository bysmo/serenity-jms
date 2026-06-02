package com.bysmo.serenity.epargne.repository;

import com.bysmo.serenity.epargne.entity.EpargnePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EpargnePlanRepository extends JpaRepository<EpargnePlan, UUID> {

    List<EpargnePlan> findByActifTrue();

    List<EpargnePlan> findByActif(Boolean actif);
}
