package com.bysmo.serenity.cotisation.service;

import com.bysmo.serenity.cotisation.client.AccountServiceClient;
import com.bysmo.serenity.cotisation.client.dto.AccountingEntryRequest;
import com.bysmo.serenity.cotisation.client.dto.MouvementCaisseResponse;
import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.dto.RemboursementRequest;
import com.bysmo.serenity.cotisation.dto.RemboursementResponse;
import com.bysmo.serenity.cotisation.entity.Cotisation;
import com.bysmo.serenity.cotisation.entity.Remboursement;
import com.bysmo.serenity.cotisation.enums.RemboursementStatut;
import com.bysmo.serenity.cotisation.repository.RemboursementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RemboursementService {

    private final RemboursementRepository remboursementRepository;
    private final CotisationService cotisationService;
    private final AccountServiceClient accountServiceClient;

    @Transactional
    public RemboursementResponse create(RemboursementRequest request) {
        log.info("Creating remboursement for membreId={}, cotisationId={}", request.getMembreId(), request.getCotisationId());

        // Verify cotisation exists
        Cotisation cotisation = cotisationService.getEntityById(request.getCotisationId());

        Remboursement remboursement = Remboursement.builder()
                .cotisationId(request.getCotisationId())
                .membreId(request.getMembreId())
                .montant(request.getMontant())
                .motif(request.getMotif())
                .statut(RemboursementStatut.EN_ATTENTE)
                .build();

        remboursement = remboursementRepository.save(remboursement);
        log.info("Remboursement created with id={}", remboursement.getId());

        // Record SORTIE mouvement in account-service
        try {
            AccountingEntryRequest sortieEntry = AccountingEntryRequest.builder()
                    .caisseId(cotisation.getCaisseId())
                    .montant(request.getMontant())
                    .sens("SORTIE")
                    .type("REMBOURSEMENT")
                    .description("Remboursement cotisation: " + cotisation.getLibelle())
                    .referenceType("REMBOURSEMENT")
                    .referenceId(remboursement.getId())
                    .build();
            ApiResponse<MouvementCaisseResponse> mouvementResponse = accountServiceClient.recordMouvement(sortieEntry);
            if (mouvementResponse != null && mouvementResponse.isSuccess()) {
                log.info("SORTIE mouvement recorded for remboursementId={}", remboursement.getId());
            } else {
                log.warn("Failed to record SORTIE mouvement for remboursementId={}", remboursement.getId());
            }
        } catch (Exception e) {
            log.error("Error recording SORTIE mouvement for remboursementId={}: {}", remboursement.getId(), e.getMessage());
        }

        return mapToResponse(remboursement);
    }

    @Transactional
    public RemboursementResponse approve(UUID id, UUID traitePar) {
        log.info("Approving remboursement id={} by {}", id, traitePar);
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remboursement non trouvé avec l'id: " + id));

        if (remboursement.getStatut() != RemboursementStatut.EN_ATTENTE) {
            throw new RuntimeException("Le remboursement ne peut être approuvé que s'il est en attente. Statut actuel: " + remboursement.getStatut());
        }

        remboursement.setStatut(RemboursementStatut.APPROUVE);
        remboursement.setTraitePar(traitePar);
        remboursement.setDateTraitement(LocalDateTime.now());
        remboursement = remboursementRepository.save(remboursement);

        log.info("Remboursement approved with id={}", id);
        return mapToResponse(remboursement);
    }

    @Transactional
    public RemboursementResponse reject(UUID id, UUID traitePar, String commentaire) {
        log.info("Rejecting remboursement id={} by {}", id, traitePar);
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remboursement non trouvé avec l'id: " + id));

        if (remboursement.getStatut() != RemboursementStatut.EN_ATTENTE) {
            throw new RuntimeException("Le remboursement ne peut être refusé que s'il est en attente. Statut actuel: " + remboursement.getStatut());
        }

        remboursement.setStatut(RemboursementStatut.REFUSE);
        remboursement.setTraitePar(traitePar);
        remboursement.setDateTraitement(LocalDateTime.now());
        remboursement.setCommentaire(commentaire);
        remboursement = remboursementRepository.save(remboursement);

        // Record reversal ENTREE mouvement since the refund was rejected
        Cotisation cotisation = cotisationService.getEntityById(remboursement.getCotisationId());
        try {
            AccountingEntryRequest reversalEntry = AccountingEntryRequest.builder()
                    .caisseId(cotisation.getCaisseId())
                    .montant(remboursement.getMontant())
                    .sens("ENTREE")
                    .type("ANNULATION_REMBOURSEMENT")
                    .description("Annulation remboursement cotisation: " + cotisation.getLibelle())
                    .referenceType("REMBOURSEMENT_ANNULATION")
                    .referenceId(remboursement.getId())
                    .build();
            ApiResponse<MouvementCaisseResponse> mouvementResponse = accountServiceClient.recordMouvement(reversalEntry);
            if (mouvementResponse != null && mouvementResponse.isSuccess()) {
                log.info("Reversal ENTREE mouvement recorded for rejected remboursementId={}", id);
            } else {
                log.warn("Failed to record reversal for rejected remboursementId={}", id);
            }
        } catch (Exception e) {
            log.error("Error recording reversal for rejected remboursementId={}: {}", id, e.getMessage());
        }

        log.info("Remboursement rejected with id={}", id);
        return mapToResponse(remboursement);
    }

    @Transactional(readOnly = true)
    public RemboursementResponse getById(UUID id) {
        log.info("Fetching remboursement by id={}", id);
        Remboursement remboursement = remboursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remboursement non trouvé avec l'id: " + id));
        return mapToResponse(remboursement);
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getByMembre(UUID membreId) {
        log.info("Fetching remboursements for membreId={}", membreId);
        return remboursementRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getByCotisation(UUID cotisationId) {
        log.info("Fetching remboursements for cotisationId={}", cotisationId);
        return remboursementRepository.findByCotisationId(cotisationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RemboursementResponse> getAll() {
        log.info("Fetching all remboursements");
        return remboursementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private RemboursementResponse mapToResponse(Remboursement remboursement) {
        return RemboursementResponse.builder()
                .id(remboursement.getId())
                .cotisationId(remboursement.getCotisationId())
                .membreId(remboursement.getMembreId())
                .montant(remboursement.getMontant())
                .motif(remboursement.getMotif())
                .statut(remboursement.getStatut())
                .traitePar(remboursement.getTraitePar())
                .dateTraitement(remboursement.getDateTraitement())
                .commentaire(remboursement.getCommentaire())
                .createdAt(remboursement.getCreatedAt())
                .updatedAt(remboursement.getUpdatedAt())
                .build();
    }
}
