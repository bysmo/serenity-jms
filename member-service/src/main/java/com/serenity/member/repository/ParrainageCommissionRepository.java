package com.serenity.member.repository;

import com.serenity.member.entity.ParrainageCommission;
import com.serenity.member.entity.enums.CommissionStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ParrainageCommissionRepository extends JpaRepository<ParrainageCommission, UUID> {

    List<ParrainageCommission> findByParrainId(UUID parrainId);

    List<ParrainageCommission> findByFilleulId(UUID filleulId);

    List<ParrainageCommission> findByStatut(CommissionStatut statut);

    List<ParrainageCommission> findByDisponibleLeBefore(LocalDateTime date);
}
