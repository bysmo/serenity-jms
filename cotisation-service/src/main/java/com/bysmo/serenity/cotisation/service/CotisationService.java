package com.bysmo.serenity.cotisation.service;

import com.bysmo.serenity.cotisation.dto.CotisationRequest;
import com.bysmo.serenity.cotisation.dto.CotisationResponse;
import com.bysmo.serenity.cotisation.entity.Cotisation;
import com.bysmo.serenity.cotisation.enums.CotisationType;
import com.bysmo.serenity.cotisation.enums.Visibilite;
import com.bysmo.serenity.cotisation.repository.CotisationRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CotisationService {

    private final CotisationRepository cotisationRepository;

    @Transactional
    public CotisationResponse create(CotisationRequest request) {
        log.info("Creating cotisation: {}", request.getLibelle());
        Cotisation cotisation = Cotisation.builder()
                .libelle(request.getLibelle())
                .description(request.getDescription())
                .type(request.getType())
                .frequence(request.getFrequence())
                .typeMontant(request.getTypeMontant())
                .montant(request.getMontant())
                .caisseId(request.getCaisseId())
                .createdByMembreId(request.getCreatedByMembreId())
                .adminMembreId(request.getAdminMembreId())
                .visibilite(request.getVisibilite() != null ? request.getVisibilite() : Visibilite.PUBLIQUE)
                .tag(request.getTag())
                .actif(request.getActif() != null ? request.getActif() : true)
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .build();
        cotisation = cotisationRepository.save(cotisation);
        log.info("Cotisation created with id={}", cotisation.getId());
        return mapToResponse(cotisation);
    }

    @Transactional(readOnly = true)
    public CotisationResponse getById(UUID id) {
        log.info("Fetching cotisation by id={}", id);
        Cotisation cotisation = cotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotisation non trouvée avec l'id: " + id));
        return mapToResponse(cotisation);
    }

    @Transactional(readOnly = true)
    public Cotisation getEntityById(UUID id) {
        return cotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotisation non trouvée avec l'id: " + id));
    }

    @Transactional(readOnly = true)
    public List<CotisationResponse> getAll() {
        log.info("Fetching all cotisations");
        return cotisationRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CotisationResponse> filter(Boolean actif, CotisationType type, Visibilite visibilite) {
        log.info("Filtering cotisations: actif={}, type={}, visibilite={}", actif, type, visibilite);
        Specification<Cotisation> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (actif != null) {
                predicates.add(cb.equal(root.get("actif"), actif));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (visibilite != null) {
                predicates.add(cb.equal(root.get("visibilite"), visibilite));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return cotisationRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CotisationResponse update(UUID id, CotisationRequest request) {
        log.info("Updating cotisation id={}", id);
        Cotisation cotisation = cotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotisation non trouvée avec l'id: " + id));

        cotisation.setLibelle(request.getLibelle());
        cotisation.setDescription(request.getDescription());
        cotisation.setType(request.getType());
        cotisation.setFrequence(request.getFrequence());
        cotisation.setTypeMontant(request.getTypeMontant());
        cotisation.setMontant(request.getMontant());
        cotisation.setCaisseId(request.getCaisseId());
        cotisation.setAdminMembreId(request.getAdminMembreId());
        if (request.getVisibilite() != null) {
            cotisation.setVisibilite(request.getVisibilite());
        }
        cotisation.setTag(request.getTag());
        if (request.getActif() != null) {
            cotisation.setActif(request.getActif());
        }
        cotisation.setDateDebut(request.getDateDebut());
        cotisation.setDateFin(request.getDateFin());

        cotisation = cotisationRepository.save(cotisation);
        log.info("Cotisation updated with id={}", cotisation.getId());
        return mapToResponse(cotisation);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting cotisation id={}", id);
        Cotisation cotisation = cotisationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotisation non trouvée avec l'id: " + id));
        cotisation.setActif(false);
        cotisationRepository.save(cotisation);
        log.info("Cotisation deactivated with id={}", id);
    }

    private CotisationResponse mapToResponse(Cotisation cotisation) {
        return CotisationResponse.builder()
                .id(cotisation.getId())
                .libelle(cotisation.getLibelle())
                .description(cotisation.getDescription())
                .type(cotisation.getType())
                .frequence(cotisation.getFrequence())
                .typeMontant(cotisation.getTypeMontant())
                .montant(cotisation.getMontant())
                .caisseId(cotisation.getCaisseId())
                .createdByMembreId(cotisation.getCreatedByMembreId())
                .adminMembreId(cotisation.getAdminMembreId())
                .visibilite(cotisation.getVisibilite())
                .tag(cotisation.getTag())
                .actif(cotisation.getActif())
                .dateDebut(cotisation.getDateDebut())
                .dateFin(cotisation.getDateFin())
                .createdAt(cotisation.getCreatedAt())
                .updatedAt(cotisation.getUpdatedAt())
                .build();
    }
}
