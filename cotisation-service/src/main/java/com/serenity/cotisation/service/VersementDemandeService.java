package com.serenity.cotisation.service;

import com.serenity.cotisation.dto.VersementDemandeRequest;
import com.serenity.cotisation.dto.VersementDemandeResponse;
import com.serenity.cotisation.entity.CotisationVersementDemande;
import com.serenity.cotisation.enums.VersementDemandeStatut;
import com.serenity.cotisation.repository.CotisationVersementDemandeRepository;
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
public class VersementDemandeService {

    private final CotisationVersementDemandeRepository versementDemandeRepository;
    private final CotisationService cotisationService;

    @Transactional
    public VersementDemandeResponse createDemande(VersementDemandeRequest request) {
        log.info("Creating versement demande for membreId={}, cotisationId={}", request.getMembreId(), request.getCotisationId());

        // Verify cotisation exists
        cotisationService.getEntityById(request.getCotisationId());

        CotisationVersementDemande demande = CotisationVersementDemande.builder()
                .cotisationId(request.getCotisationId())
                .membreId(request.getMembreId())
                .montantDemande(request.getMontantDemande())
                .statut(VersementDemandeStatut.EN_ATTENTE)
                .build();

        demande = versementDemandeRepository.save(demande);
        log.info("Versement demande created with id={}", demande.getId());
        return mapToResponse(demande);
    }

    @Transactional
    public VersementDemandeResponse traite(UUID demandeId, UUID traitePar, VersementDemandeStatut statut) {
        log.info("Processing versement demande id={} to statut={} by {}", demandeId, statut, traitePar);
        CotisationVersementDemande demande = versementDemandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Versement demande non trouvée avec l'id: " + demandeId));

        if (demande.getStatut() != VersementDemandeStatut.EN_ATTENTE) {
            throw new RuntimeException("La demande a déjà été traitée. Statut actuel: " + demande.getStatut());
        }

        demande.setStatut(statut);
        demande.setTraitePar(traitePar);
        demande.setDateTraitement(LocalDateTime.now());
        demande = versementDemandeRepository.save(demande);

        log.info("Versement demande processed with id={}, statut={}", demandeId, statut);
        return mapToResponse(demande);
    }

    @Transactional
    public VersementDemandeResponse reject(UUID demandeId, UUID traitePar, String motifRejet) {
        log.info("Rejecting versement demande id={} by {}", demandeId, traitePar);
        CotisationVersementDemande demande = versementDemandeRepository.findById(demandeId)
                .orElseThrow(() -> new RuntimeException("Versement demande non trouvée avec l'id: " + demandeId));

        if (demande.getStatut() != VersementDemandeStatut.EN_ATTENTE) {
            throw new RuntimeException("La demande a déjà été traitée. Statut actuel: " + demande.getStatut());
        }

        demande.setStatut(VersementDemandeStatut.REJETE);
        demande.setTraitePar(traitePar);
        demande.setDateTraitement(LocalDateTime.now());
        demande.setMotifRejet(motifRejet);
        demande = versementDemandeRepository.save(demande);

        log.info("Versement demande rejected with id={}", demandeId);
        return mapToResponse(demande);
    }

    @Transactional(readOnly = true)
    public VersementDemandeResponse getById(UUID id) {
        log.info("Fetching versement demande by id={}", id);
        CotisationVersementDemande demande = versementDemandeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Versement demande non trouvée avec l'id: " + id));
        return mapToResponse(demande);
    }

    @Transactional(readOnly = true)
    public List<VersementDemandeResponse> getByMembre(UUID membreId) {
        log.info("Fetching versement demandes for membreId={}", membreId);
        return versementDemandeRepository.findByMembreId(membreId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VersementDemandeResponse> getByCotisation(UUID cotisationId) {
        log.info("Fetching versement demandes for cotisationId={}", cotisationId);
        return versementDemandeRepository.findByCotisationId(cotisationId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VersementDemandeResponse> getAll() {
        log.info("Fetching all versement demandes");
        return versementDemandeRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private VersementDemandeResponse mapToResponse(CotisationVersementDemande demande) {
        return VersementDemandeResponse.builder()
                .id(demande.getId())
                .cotisationId(demande.getCotisationId())
                .membreId(demande.getMembreId())
                .montantDemande(demande.getMontantDemande())
                .statut(demande.getStatut())
                .traitePar(demande.getTraitePar())
                .dateTraitement(demande.getDateTraitement())
                .motifRejet(demande.getMotifRejet())
                .createdAt(demande.getCreatedAt())
                .updatedAt(demande.getUpdatedAt())
                .build();
    }
}
