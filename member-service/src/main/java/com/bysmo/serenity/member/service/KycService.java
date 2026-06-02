package com.bysmo.serenity.member.service;

import com.bysmo.serenity.member.dto.KycDocumentRequest;
import com.bysmo.serenity.member.dto.KycDocumentResponse;
import com.bysmo.serenity.member.dto.KycVerificationRequest;
import com.bysmo.serenity.member.dto.KycVerificationResponse;
import com.bysmo.serenity.member.entity.KycDocument;
import com.bysmo.serenity.member.entity.KycVerification;
import com.bysmo.serenity.member.entity.Membre;
import com.bysmo.serenity.member.entity.enums.KycStatut;
import com.bysmo.serenity.member.event.MemberEventPublisher;
import com.bysmo.serenity.member.exception.BusinessException;
import com.bysmo.serenity.member.exception.ResourceNotFoundException;
import com.bysmo.serenity.member.mapper.KycDocumentMapper;
import com.bysmo.serenity.member.mapper.KycVerificationMapper;
import com.bysmo.serenity.member.repository.KycDocumentRepository;
import com.bysmo.serenity.member.repository.KycVerificationRepository;
import com.bysmo.serenity.member.repository.MembreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KycService {

    private final KycVerificationRepository kycVerificationRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final MembreRepository membreRepository;
    private final KycVerificationMapper kycVerificationMapper;
    private final KycDocumentMapper kycDocumentMapper;
    private final MemberEventPublisher memberEventPublisher;

    @Transactional
    public KycVerificationResponse initiateVerification(UUID membreId) {
        log.info("Initiating KYC verification for member: {}", membreId);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", membreId));

        List<KycVerification> existing = kycVerificationRepository.findByMembreId(membreId);
        boolean hasPending = existing.stream()
                .anyMatch(v -> v.getStatut() == KycStatut.EN_ATTENTE);
        if (hasPending) {
            throw new BusinessException("Une vérification KYC est déjà en cours pour ce membre");
        }

        KycVerification verification = KycVerification.builder()
                .membreId(membreId)
                .statut(KycStatut.EN_ATTENTE)
                .niveau("LEVEL_1")
                .build();

        verification = kycVerificationRepository.save(verification);

        membre.setKycNiveau("PENDING");
        membreRepository.save(membre);

        log.info("KYC verification initiated with id: {} for member: {}", verification.getId(), membreId);
        return kycVerificationMapper.toResponse(verification);
    }

    @Transactional
    public KycDocumentResponse uploadDocument(UUID kycVerificationId, KycDocumentRequest request) {
        log.info("Uploading KYC document for verification: {}", kycVerificationId);

        KycVerification verification = kycVerificationRepository.findById(kycVerificationId)
                .orElseThrow(() -> new ResourceNotFoundException("KycVerification", "id", kycVerificationId));

        if (verification.getStatut() != KycStatut.EN_ATTENTE) {
            throw new BusinessException("Impossible d'ajouter un document à une vérification non en attente");
        }

        KycDocument document = kycDocumentMapper.toEntity(request);
        document.setKycVerificationId(kycVerificationId);
        document.setUploadedAt(LocalDateTime.now());

        document = kycDocumentRepository.save(document);

        log.info("KYC document uploaded with id: {} for verification: {}", document.getId(), kycVerificationId);
        return kycDocumentMapper.toResponse(document);
    }

    @Transactional
    public KycVerificationResponse validate(UUID membreId, UUID validatedBy) {
        log.info("Validating KYC for member: {} by {}", membreId, validatedBy);

        KycVerification verification = findLatestPendingVerification(membreId);

        verification.setStatut(KycStatut.VALIDE);
        verification.setValidatedBy(validatedBy);
        verification.setValidatedAt(LocalDateTime.now());

        verification = kycVerificationRepository.save(verification);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", membreId));
        membre.setKycNiveau(verification.getNiveau());
        membreRepository.save(membre);

        memberEventPublisher.publishMemberKycValidated(membreId, validatedBy);

        log.info("KYC validated for member: {}", membreId);
        return buildVerificationResponse(verification);
    }

    @Transactional
    public KycVerificationResponse reject(UUID membreId, UUID rejectedBy, String motif) {
        log.info("Rejecting KYC for member: {} by {}", membreId, rejectedBy);

        KycVerification verification = findLatestPendingVerification(membreId);

        verification.setStatut(KycStatut.REJETE);
        verification.setRejectedBy(rejectedBy);
        verification.setRejectedAt(LocalDateTime.now());
        verification.setMotifRejet(motif);

        verification = kycVerificationRepository.save(verification);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", membreId));
        membre.setKycNiveau("NONE");
        membreRepository.save(membre);

        log.info("KYC rejected for member: {} with motif: {}", membreId, motif);
        return buildVerificationResponse(verification);
    }

    @Transactional(readOnly = true)
    public KycVerificationResponse getByMembre(UUID membreId) {
        log.debug("Fetching KYC status for member: {}", membreId);

        List<KycVerification> verifications = kycVerificationRepository.findByMembreId(membreId);
        if (verifications.isEmpty()) {
            throw new ResourceNotFoundException("KycVerification", "membreId", membreId);
        }

        KycVerification latest = verifications.get(verifications.size() - 1);
        return buildVerificationResponse(latest);
    }

    private KycVerification findLatestPendingVerification(UUID membreId) {
        List<KycVerification> verifications = kycVerificationRepository.findByMembreId(membreId);
        return verifications.stream()
                .filter(v -> v.getStatut() == KycStatut.EN_ATTENTE)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new BusinessException("Aucune vérification KYC en attente pour ce membre"));
    }

    private KycVerificationResponse buildVerificationResponse(KycVerification verification) {
        List<KycDocument> documents = kycDocumentRepository.findByKycVerificationId(verification.getId());
        List<KycDocumentResponse> documentResponses = kycDocumentMapper.toResponseList(documents);

        KycVerificationResponse response = kycVerificationMapper.toResponse(verification);
        response.setDocuments(documentResponses);
        return response;
    }
}
