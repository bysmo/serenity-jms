package com.serenity.epargne.service;

import com.serenity.epargne.client.AccountServiceClient;
import com.serenity.epargne.client.dto.AccountingEntryRequest;
import com.serenity.epargne.client.dto.ApiResponse;
import com.serenity.epargne.client.dto.MouvementCaisseResponse;
import com.serenity.epargne.entity.EpargneEcheance;
import com.serenity.epargne.entity.EpargneSouscription;
import com.serenity.epargne.entity.EpargneVersement;
import com.serenity.epargne.enums.EcheanceStatut;
import com.serenity.epargne.enums.SouscriptionStatut;
import com.serenity.epargne.repository.EpargneVersementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpargneVersementService {

    private final EpargneVersementRepository versementRepository;
    private final EpargneSouscriptionService souscriptionService;
    private final EpargneEcheanceService echeanceService;
    private final AccountServiceClient accountServiceClient;

    @Transactional
    public EpargneVersement verse(UUID souscriptionId, UUID echeanceId, BigDecimal montant,
                                   String modePaiement) {
        log.info("Processing versement: souscription={}, echeance={}, montant={}",
                souscriptionId, echeanceId, montant);

        // Validate souscription
        EpargneSouscription souscription = souscriptionService.getById(souscriptionId);
        if (souscription.getStatut() == SouscriptionStatut.ANNULEE) {
            throw new RuntimeException("Impossible de verser sur une souscription annulée");
        }
        if (souscription.getStatut() == SouscriptionStatut.TERMINEE) {
            throw new RuntimeException("Impossible de verser sur une souscription terminée");
        }

        // Create versement
        EpargneVersement versement = new EpargneVersement();
        versement.setSouscription(souscription);
        versement.setMontant(montant);
        versement.setDateVersement(LocalDateTime.now());
        versement.setModePaiement(modePaiement);
        versement.setReference(generateReference(souscriptionId));

        // If echeance is specified, link and update it
        if (echeanceId != null) {
            EpargneEcheance echeance = echeanceService.getById(echeanceId);
            if (!echeance.getSouscriptionId().equals(souscriptionId)) {
                throw new RuntimeException("L'échéance ne correspond pas à la souscription");
            }
            if (echeance.getStatut() == EcheanceStatut.PAYEE) {
                throw new RuntimeException("L'échéance est déjà entièrement payée");
            }
            versement.setEcheance(echeance);

            // Update echeance
            echeanceService.updateEcheanceOnVersement(echeanceId, montant);
        }

        versement = versementRepository.save(versement);

        // Record ENTREE mouvement in account-service (tontine caisse)
        recordMouvement(souscription, montant, versement);

        log.info("Versement created successfully: {}", versement.getId());
        return versement;
    }

    @Transactional(readOnly = true)
    public List<EpargneVersement> getBySouscription(UUID souscriptionId) {
        log.debug("Fetching versements for souscription: {}", souscriptionId);
        return versementRepository.findBySouscriptionIdOrderByDateVersementDesc(souscriptionId);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalVerse(UUID souscriptionId) {
        log.debug("Calculating total versements for souscription: {}", souscriptionId);
        return versementRepository.sumMontantBySouscriptionId(souscriptionId);
    }

    @Transactional(readOnly = true)
    public EpargneVersement getById(UUID id) {
        return versementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versement non trouvé avec l'id: " + id));
    }

    private void recordMouvement(EpargneSouscription souscription, BigDecimal montant,
                                  EpargneVersement versement) {
        if (souscription.getCaisseId() == null) {
            log.warn("No caisse ID for souscription {}, skipping mouvement recording",
                    souscription.getId());
            return;
        }

        try {
            AccountingEntryRequest entryRequest = new AccountingEntryRequest(
                    souscription.getCaisseId(),
                    "ENTREE",
                    montant,
                    String.format("Versement épargne - Souscription %s",
                            souscription.getId()),
                    versement.getId().toString(),
                    versement.getId(),
                    "VERSEMENT_EPARGNE"
            );

            ApiResponse<MouvementCaisseResponse> response = accountServiceClient.recordMouvement(entryRequest);
            if (response != null && response.isSuccess()) {
                log.info("Mouvement ENTREE recorded successfully for versement: {}", versement.getId());
            } else {
                log.warn("Failed to record mouvement for versement: {}", versement.getId());
            }
        } catch (Exception e) {
            log.error("Error recording mouvement for versement {}: {}",
                    versement.getId(), e.getMessage());
        }
    }

    private String generateReference(UUID souscriptionId) {
        return "VER-" + souscriptionId.toString().substring(0, 8).toUpperCase()
                + "-" + System.currentTimeMillis() % 100000;
    }
}
