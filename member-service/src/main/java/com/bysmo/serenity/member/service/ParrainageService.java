package com.bysmo.serenity.member.service;

import com.bysmo.serenity.member.dto.ParrainageCommissionResponse;
import com.bysmo.serenity.member.dto.ParrainageConfigRequest;
import com.bysmo.serenity.member.dto.ParrainageConfigResponse;
import com.bysmo.serenity.member.entity.Membre;
import com.bysmo.serenity.member.entity.ParrainageCommission;
import com.bysmo.serenity.member.entity.ParrainageConfig;
import com.bysmo.serenity.member.entity.enums.CommissionStatut;
import com.bysmo.serenity.member.entity.enums.DeclencheurParrainage;
import com.bysmo.serenity.member.entity.enums.TypeRemuneration;
import com.bysmo.serenity.member.exception.BusinessException;
import com.bysmo.serenity.member.exception.ResourceNotFoundException;
import com.bysmo.serenity.member.mapper.ParrainageCommissionMapper;
import com.bysmo.serenity.member.mapper.ParrainageConfigMapper;
import com.bysmo.serenity.member.repository.MembreRepository;
import com.bysmo.serenity.member.repository.ParrainageCommissionRepository;
import com.bysmo.serenity.member.repository.ParrainageConfigRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParrainageService {

    private final ParrainageConfigRepository parrainageConfigRepository;
    private final ParrainageCommissionRepository parrainageCommissionRepository;
    private final MembreRepository membreRepository;
    private final ParrainageConfigMapper parrainageConfigMapper;
    private final ParrainageCommissionMapper parrainageCommissionMapper;

    @Transactional(readOnly = true)
    public ParrainageConfigResponse getConfig() {
        log.debug("Fetching current parrainage config");
        ParrainageConfig config = parrainageConfigRepository.findTopByOrderByCreatedAtDesc()
                .orElseGet(() -> ParrainageConfig.builder()
                        .actif(false)
                        .typeRemuneration(TypeRemuneration.FIXE)
                        .montantFixe(BigDecimal.ZERO)
                        .pourcentage(BigDecimal.ZERO)
                        .declencheur(DeclencheurParrainage.INSCRIPTION)
                        .niveauMax(1)
                        .delaiDisponibiliteJours(30)
                        .plafondMensuel(BigDecimal.ZERO)
                        .build());
        return parrainageConfigMapper.toResponse(config);
    }

    @Transactional
    public ParrainageConfigResponse updateConfig(ParrainageConfigRequest request) {
        log.info("Updating parrainage config");

        ParrainageConfig config = ParrainageConfig.builder()
                .actif(request.getActif())
                .typeRemuneration(request.getTypeRemuneration())
                .montantFixe(request.getMontantFixe())
                .pourcentage(request.getPourcentage())
                .declencheur(request.getDeclencheur())
                .niveauMax(request.getNiveauMax())
                .delaiDisponibiliteJours(request.getDelaiDisponibiliteJours())
                .plafondMensuel(request.getPlafondMensuel())
                .description(request.getDescription())
                .build();

        config = parrainageConfigRepository.save(config);

        log.info("Parrainage config updated with id: {}", config.getId());
        return parrainageConfigMapper.toResponse(config);
    }

    @Transactional
    public String generateReferralCode(UUID membreId) {
        log.info("Generating referral code for member: {}", membreId);

        Membre membre = membreRepository.findById(membreId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", membreId));

        if (membre.getCodeParrainage() != null) {
            return membre.getCodeParrainage();
        }

        String code = generateUniqueCode();
        membre.setCodeParrainage(code);
        membre.setParrainageActif(true);
        membreRepository.save(membre);

        log.info("Referral code generated for member {}: {}", membreId, code);
        return code;
    }

    @Transactional
    public void processReferral(UUID filleulId, String codeParrainage) {
        log.info("Processing referral for filleul: {} with code: {}", filleulId, codeParrainage);

        Membre parrain = membreRepository.findByCodeParrainage(codeParrainage)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "codeParrainage", codeParrainage));

        Membre filleul = membreRepository.findById(filleulId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", filleulId));

        if (filleul.getParrainId() != null) {
            throw new BusinessException("Ce membre a déjà un parrain");
        }

        if (filleulId.equals(parrain.getId())) {
            throw new BusinessException("Un membre ne peut pas être son propre parrain");
        }

        filleul.setParrainId(parrain.getId());
        filleul.setNiveauParrainage(parrain.getNiveauParrainage() + 1);
        membreRepository.save(filleul);

        ParrainageConfig config = parrainageConfigRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException("Aucune configuration de parrainage trouvée"));

        if (config.getActif() && config.getDeclencheur() == DeclencheurParrainage.INSCRIPTION) {
            processMultiLevelCommissions(parrain, filleul, config, DeclencheurParrainage.INSCRIPTION, BigDecimal.ZERO);
        }

        log.info("Referral processed: filleul {} linked to parrain {}", filleulId, parrain.getId());
    }

    @Transactional
    public void processCommission(UUID parrainId, UUID filleulId, String declencheur, BigDecimal montant) {
        log.info("Processing commission for parrain: {} filleul: {} declencheur: {} montant: {}",
                parrainId, filleulId, declencheur, montant);

        ParrainageConfig config = parrainageConfigRepository.findTopByOrderByCreatedAtDesc()
                .orElseThrow(() -> new BusinessException("Aucune configuration de parrainage trouvée"));

        if (!config.getActif()) {
            log.info("Parrainage is not active, skipping commission");
            return;
        }

        DeclencheurParrainage declencheurParrainage = DeclencheurParrainage.valueOf(declencheur);
        if (config.getDeclencheur() != declencheurParrainage) {
            log.info("Declencheur {} does not match config {}, skipping commission", declencheur, config.getDeclencheur());
            return;
        }

        Membre parrain = membreRepository.findById(parrainId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", parrainId));
        Membre filleul = membreRepository.findById(filleulId)
                .orElseThrow(() -> new ResourceNotFoundException("Membre", "id", filleulId));

        processMultiLevelCommissions(parrain, filleul, config, declencheurParrainage, montant);

        log.info("Commission processed for parrain: {}", parrainId);
    }

    private void processMultiLevelCommissions(Membre directParrain, Membre filleul,
                                               ParrainageConfig config,
                                               DeclencheurParrainage declencheur,
                                               BigDecimal montantTransaction) {
        List<ParrainageCommission> commissions = new ArrayList<>();
        Membre currentParrain = directParrain;
        int niveau = 1;

        while (currentParrain != null && niveau <= config.getNiveauMax()) {
            BigDecimal montantCommission = calculateCommissionAmount(config, montantTransaction, niveau);

            if (montantCommission.compareTo(BigDecimal.ZERO) > 0) {
                LocalDateTime disponibleLe = LocalDateTime.now().plusDays(config.getDelaiDisponibiliteJours());

                ParrainageCommission commission = ParrainageCommission.builder()
                        .parrainId(currentParrain.getId())
                        .filleulId(filleul.getId())
                        .configId(config.getId())
                        .niveau(niveau)
                        .declencheur(declencheur)
                        .montant(montantCommission)
                        .statut(CommissionStatut.EN_ATTENTE)
                        .disponibleLe(disponibleLe)
                        .build();

                commissions.add(commission);
            }

            // Move up to the next level parrain
            if (currentParrain.getParrainId() != null) {
                currentParrain = membreRepository.findById(currentParrain.getParrainId()).orElse(null);
            } else {
                currentParrain = null;
            }
            niveau++;
        }

        parrainageCommissionRepository.saveAll(commissions);
        log.info("Created {} commission entries for multi-level parrainage", commissions.size());
    }

    private BigDecimal calculateCommissionAmount(ParrainageConfig config, BigDecimal montantTransaction, int niveau) {
        BigDecimal montant;

        if (config.getTypeRemuneration() == TypeRemuneration.FIXE) {
            montant = config.getMontantFixe();
            // For multi-level: reduce amount for higher levels (50% for level 2, 25% for level 3, etc.)
            if (niveau > 1) {
                BigDecimal reduction = BigDecimal.valueOf(1.0 / Math.pow(2, niveau - 1));
                montant = montant.multiply(reduction).setScale(2, RoundingMode.HALF_UP);
            }
        } else {
            // POURCENTAGE
            montant = montantTransaction.multiply(config.getPourcentage())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            if (niveau > 1) {
                BigDecimal reduction = BigDecimal.valueOf(1.0 / Math.pow(2, niveau - 1));
                montant = montant.multiply(reduction).setScale(2, RoundingMode.HALF_UP);
            }
        }

        // Check monthly cap
        if (config.getPlafondMensuel().compareTo(BigDecimal.ZERO) > 0) {
            LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            BigDecimal totalThisMonth = parrainageCommissionRepository.findByParrainId(config.getId()).stream()
                    .filter(c -> c.getCreatedAt().isAfter(startOfMonth))
                    .map(ParrainageCommission::getMontant)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal remaining = config.getPlafondMensuel().subtract(totalThisMonth);
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }
            montant = montant.min(remaining);
        }

        return montant;
    }

    @Transactional(readOnly = true)
    public List<ParrainageCommissionResponse> getCommissionsByParrain(UUID parrainId) {
        log.debug("Fetching commissions for parrain: {}", parrainId);
        List<ParrainageCommission> commissions = parrainageCommissionRepository.findByParrainId(parrainId);
        return parrainageCommissionMapper.toResponseList(commissions);
    }

    @Transactional
    public ParrainageCommissionResponse claimCommission(UUID commissionId) {
        log.info("Claiming commission: {}", commissionId);

        ParrainageCommission commission = parrainageCommissionRepository.findById(commissionId)
                .orElseThrow(() -> new ResourceNotFoundException("ParrainageCommission", "id", commissionId));

        if (commission.getStatut() != CommissionStatut.DISPONIBLE) {
            throw new BusinessException("Seules les commissions disponibles peuvent être réclamées. Statut actuel: " + commission.getStatut());
        }

        commission.setStatut(CommissionStatut.RECLAME);
        commission.setReclameLe(LocalDateTime.now());
        commission = parrainageCommissionRepository.save(commission);

        log.info("Commission claimed: {}", commissionId);
        return parrainageCommissionMapper.toResponse(commission);
    }

    @Scheduled(cron = "0 0 6 * * ?")
    @Transactional
    public void activatePendingCommissions() {
        log.info("Scheduled task: activating pending commissions");

        LocalDateTime now = LocalDateTime.now();
        List<ParrainageCommission> pendingCommissions = parrainageCommissionRepository
                .findByDisponibleLeBefore(now);

        int activated = 0;
        for (ParrainageCommission commission : pendingCommissions) {
            if (commission.getStatut() == CommissionStatut.EN_ATTENTE) {
                commission.setStatut(CommissionStatut.DISPONIBLE);
                activated++;
            }
        }

        if (activated > 0) {
            parrainageCommissionRepository.saveAll(pendingCommissions);
            log.info("Activated {} pending commissions", activated);
        } else {
            log.info("No pending commissions to activate");
        }
    }

    private String generateUniqueCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        // Ensure uniqueness
        while (membreRepository.findByCodeParrainage(code.toString()).isPresent()) {
            code = new StringBuilder();
            for (int i = 0; i < 8; i++) {
                code.append(chars.charAt(random.nextInt(chars.length())));
            }
        }

        return code.toString();
    }
}
