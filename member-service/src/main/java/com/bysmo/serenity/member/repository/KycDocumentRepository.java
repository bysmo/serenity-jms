package com.bysmo.serenity.member.repository;

import com.bysmo.serenity.member.entity.KycDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface KycDocumentRepository extends JpaRepository<KycDocument, UUID> {

    List<KycDocument> findByKycVerificationId(UUID kycVerificationId);
}
