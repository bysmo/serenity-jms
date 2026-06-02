package com.bysmo.serenity.epargne.service;

import com.bysmo.serenity.epargne.dto.EpargnePlanDto;
import com.bysmo.serenity.epargne.entity.EpargnePlan;
import com.bysmo.serenity.epargne.enums.EpargneFrequence;
import com.bysmo.serenity.epargne.repository.EpargnePlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpargnePlanService {

    private final EpargnePlanRepository epargnePlanRepository;

    @Transactional(readOnly = true)
    public List<EpargnePlan> getAll() {
        log.debug("Fetching all epargne plans");
        return epargnePlanRepository.findAll();
    }

    @Transactional(readOnly = true)
    public EpargnePlan getById(UUID id) {
        log.debug("Fetching epargne plan by id: {}", id);
        return epargnePlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan d'épargne non trouvé avec l'id: " + id));
    }

    @Transactional
    public EpargnePlan create(EpargnePlanDto.CreateRequest request) {
        log.info("Creating new epargne plan: {}", request.getNom());

        EpargnePlan plan = new EpargnePlan();
        plan.setNom(request.getNom());
        plan.setDescription(request.getDescription());
        plan.setMontantMin(request.getMontantMin());
        plan.setMontantMax(request.getMontantMax());
        plan.setFrequence(EpargneFrequence.valueOf(request.getFrequence().toUpperCase()));
        plan.setTauxRemuneration(request.getTauxRemuneration());
        plan.setDureeMois(request.getDureeMois());
        plan.setHeureLimitePaiement(request.getHeureLimitePaiement());
        plan.setDelaiRappelHeures(request.getDelaiRappelHeures());
        plan.setIntervalleRappelMinutes(request.getIntervalleRappelMinutes());
        plan.setActif(true);

        return epargnePlanRepository.save(plan);
    }

    @Transactional
    public EpargnePlan update(UUID id, EpargnePlanDto.UpdateRequest request) {
        log.info("Updating epargne plan: {}", id);

        EpargnePlan plan = getById(id);

        if (request.getNom() != null) {
            plan.setNom(request.getNom());
        }
        if (request.getDescription() != null) {
            plan.setDescription(request.getDescription());
        }
        if (request.getMontantMin() != null) {
            plan.setMontantMin(request.getMontantMin());
        }
        if (request.getMontantMax() != null) {
            plan.setMontantMax(request.getMontantMax());
        }
        if (request.getFrequence() != null) {
            plan.setFrequence(EpargneFrequence.valueOf(request.getFrequence().toUpperCase()));
        }
        if (request.getTauxRemuneration() != null) {
            plan.setTauxRemuneration(request.getTauxRemuneration());
        }
        if (request.getDureeMois() != null) {
            plan.setDureeMois(request.getDureeMois());
        }
        if (request.getHeureLimitePaiement() != null) {
            plan.setHeureLimitePaiement(request.getHeureLimitePaiement());
        }
        if (request.getDelaiRappelHeures() != null) {
            plan.setDelaiRappelHeures(request.getDelaiRappelHeures());
        }
        if (request.getIntervalleRappelMinutes() != null) {
            plan.setIntervalleRappelMinutes(request.getIntervalleRappelMinutes());
        }
        if (request.getChecksum() != null) {
            plan.setChecksum(request.getChecksum());
        }

        return epargnePlanRepository.save(plan);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting epargne plan: {}", id);
        EpargnePlan plan = getById(id);
        epargnePlanRepository.delete(plan);
    }

    @Transactional
    public EpargnePlan toggleActive(UUID planId, boolean active) {
        log.info("Toggling epargne plan {} to active={}", planId, active);
        EpargnePlan plan = getById(planId);
        plan.setActif(active);
        return epargnePlanRepository.save(plan);
    }
}
