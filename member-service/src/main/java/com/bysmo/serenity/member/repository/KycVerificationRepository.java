package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.KycVerification;
import com.bysmo.serenity.member.entity.enums.KycStatut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KycVerificationRepository extends JpaRepository<KycVerification, UUID> {

    List<KycVerification> findByMembreId(UUID membreId);

    List<KycVerification> findByStatut(KycStatut statut);
}
