package com.bysmo.serenity.cotisation.service;

import com.bysmo.serenity.cotisation.client.AccountServiceClient;
import com.bysmo.serenity.cotisation.client.MemberServiceClient;
import com.bysmo.serenity.cotisation.client.dto.AccountingEntryRequest;
import com.bysmo.serenity.cotisation.client.dto.MembreSummaryResponse;
import com.bysmo.serenity.cotisation.client.dto.MouvementCaisseResponse;
import com.bysmo.serenity.cotisation.dto.ApiResponse;
import com.bysmo.serenity.cotisation.dto.PaiementRequest;
import com.bysmo.serenity.cotisation.dto.PaiementResponse;
import com.bysmo.serenity.cotisation.entity.Cotisation;
import com.bysmo.serenity.cotisation.entity.Paiement;
import com.bysmo.serenity.cotisation.enums.PaiementStatut;
import com.bysmo.serenity.cotisation.event.CotisationEventPublisher;
import com.bysmo.serenity.cotisation.repository.PaiementRepository;
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
public class PaiementService {

    private final PaiementRepository paiementRepository;
    private final CotisationService cotisationService;
    private final MemberServiceClient memberServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final CotisationEventPublisher eventPublisher;

    @Transactional
    public PaiementResponse create(PaiementRequest request) {
        log.info("Creating paiement for membreId={}, cotisationId={}", request.getMembreId(), request.getCotisationId());

        // Verify member exists via Feign
        try {
            ApiResponse<MembreSummaryResponse> memberResponse = memberServiceClient.getMemberById(request.getMembreId());
            if (memberResponse == null || !memberResponse.isSuccess() || memberResponse.getData() == null) {
                throw new RuntimeException("Membre non trouvé avec l'id: " + request.getMembreId());
            }
            log.info("Member verified: {} {}", memberResponse.getData().getNom(), memberResponse.getData().getPrenom());
        } catch (Exception e) {
            log.error("Error verifying member with id={}: {}", request.getMembreId(), e.getMessage());
            throw new RuntimeException("Erreur lors de la vérification du membre: " + e.getMessage());
        }

        // Verify cotisation exists
        Cotisation cotisation = cotisationService.getEntityById(request.getCotisationId());
        if (!cotisation.getActif()) {
            throw new RuntimeException("La cotisation n'est plus active: " + request.getCotisationId());
        }

        // Create paiement
        Paiement paiement = Paiement.builder()
                .cotisationId(request.getCotisationId())
                .membreId(request.getMembreId())
                .montant(request.getMontant())
                .modePaiement(request.getModePaiement())
                .statut(PaiementStatut.CONFIRME)
                .reference(request.getReference())
                .walletAliasId(request.getWalletAliasId())
                .compteExterneId(request.getCompteExterneId())
                .metadata(request.getMetadata())
                .datePaiement(LocalDateTime.now())
                .build();

        paiement = paiementRepository.save(paiement);
        log.info("Paiement created with id={}", paiement.getId());

        // Record double-entry in account-service (ENTREE in cotisation caisse)
        try {
            AccountingEntryRequest accountingEntry = AccountingEntryRequest.builder()
                    .caisseId(cotisation.getCaisseId())
                    .montant(request.getMontant())
                    .sens("ENTREE")
                    .type("COTISATION")
                    .description("Paiement cotisation: " + cotisation.getLibelle())
                    .referenceType("PAIEMENT")
                    .referenceId(paiement.getId())
                    .build();
            ApiResponse<MouvementCaisseResponse> mouvementResponse = accountServiceClient.recordMouvement(accountingEntry);
            if (mouvementResponse != null && mouvementResponse.isSuccess()) {
                log.info("Mouvement caisse recorded successfully for paiementId={}", paiement.getId());
            } else {
                log.warn("Failed to record mouvement caisse for paiementId={}", paiement.getId());
            }
        } catch (Exception e) {
            log.error("Error recording mouvement caisse for paiementId={}: {}", paiement.getId(), e.getMessage());
            // Don't fail the paiement creation, just log the error
        }

        // Publish PaymentCompletedEvent
        eventPublisher.publishPaymentCompleted(paiement);

        return mapToResponse(paiement);
    }

    @Transactional(readOnly = true)
    public PaiementResponse getById(UUID id) {
        log.info("Fetching paiement by id={}", id);
        Paiement paiement = paiementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id: " + id));
        return mapToResponse(paiement);
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getByMembre(UUID membreId) {
        log.info("Fetching paiements for membreId={}", membreId);
        return paiementRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getByCotisation(UUID cotisationId) {
        log.info("Fetching paiements for cotisationId={}", cotisationId);
        return paiementRepository.findByCotisationId(cotisationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PaiementResponse> getAll() {
        log.info("Fetching all paiements");
        return paiementRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PaiementResponse cancel(UUID paiementId) {
        log.info("Cancelling paiement id={}", paiementId);
        Paiement paiement = paiementRepository.findById(paiementId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé avec l'id: " + paiementId));

        if (paiement.getStatut() == PaiementStatut.ANNULE) {
            throw new RuntimeException("Le paiement est déjà annulé: " + paiementId);
        }

        paiement.setStatut(PaiementStatut.ANNULE);
        paiement.setDateTraitement(LocalDateTime.now());
        paiement = paiementRepository.save(paiement);

        // Record reversal mouvement in account-service
        Cotisation cotisation = cotisationService.getEntityById(paiement.getCotisationId());
        try {
            AccountingEntryRequest reversalEntry = AccountingEntryRequest.builder()
                    .caisseId(cotisation.getCaisseId())
                    .montant(paiement.getMontant())
                    .sens("SORTIE")
                    .type("ANNULATION_PAIEMENT")
                    .description("Annulation paiement cotisation: " + cotisation.getLibelle())
                    .referenceType("PAIEMENT_ANNULATION")
                    .referenceId(paiement.getId())
                    .build();
            ApiResponse<MouvementCaisseResponse> mouvementResponse = accountServiceClient.recordMouvement(reversalEntry);
            if (mouvementResponse != null && mouvementResponse.isSuccess()) {
                log.info("Reversal mouvement caisse recorded for paiementId={}", paiementId);
            } else {
                log.warn("Failed to record reversal mouvement for paiementId={}", paiementId);
            }
        } catch (Exception e) {
            log.error("Error recording reversal mouvement for paiementId={}: {}", paiementId, e.getMessage());
        }

        log.info("Paiement cancelled with id={}", paiementId);
        return mapToResponse(paiement);
    }

    private PaiementResponse mapToResponse(Paiement paiement) {
        return PaiementResponse.builder()
                .id(paiement.getId())
                .cotisationId(paiement.getCotisationId())
                .membreId(paiement.getMembreId())
                .montant(paiement.getMontant())
                .modePaiement(paiement.getModePaiement())
                .statut(paiement.getStatut())
                .reference(paiement.getReference())
                .walletAliasId(paiement.getWalletAliasId())
                .compteExterneId(paiement.getCompteExterneId())
                .metadata(paiement.getMetadata())
                .datePaiement(paiement.getDatePaiement())
                .traitePar(paiement.getTraitePar())
                .dateTraitement(paiement.getDateTraitement())
                .createdAt(paiement.getCreatedAt())
                .updatedAt(paiement.getUpdatedAt())
                .build();
    }
}
