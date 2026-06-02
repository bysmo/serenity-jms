package com.serenity.account.service;

import com.serenity.account.dto.AccountingEntryRequest;
import com.serenity.account.dto.CaisseBalanceResponse;
import com.serenity.account.dto.JournalCaisseResponse;
import com.serenity.account.dto.MouvementCaisseResponse;
import com.serenity.account.entity.Caisse;
import com.serenity.account.entity.MouvementCaisse;
import com.serenity.account.entity.enums.CaisseStatut;
import com.serenity.account.entity.enums.CaisseType;
import com.serenity.account.entity.enums.Sens;
import com.serenity.account.mapper.MouvementCaisseMapper;
import com.serenity.account.repository.CaisseRepository;
import com.serenity.account.repository.MouvementCaisseRepository;
import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.common.exception.UnbalancedEntryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class FinanceService {

    private static final BigDecimal BALANCE_TOLERANCE = new BigDecimal("0.01");

    private final CaisseRepository caisseRepository;
    private final MouvementCaisseRepository mouvementCaisseRepository;
    private final MouvementCaisseMapper mouvementCaisseMapper;

    /**
     * Record a double-entry accounting operation.
     * Entries must balance: sum of ENTREE must equal sum of SORTIE within tolerance.
     */
    public void recordDoubleEntry(List<AccountingEntryRequest> entries) {
        if (entries == null || entries.isEmpty()) {
            throw new IllegalArgumentException("Les écritures comptables ne peuvent pas être vides");
        }

        BigDecimal balance = entries.stream()
                .map(e -> e.getSens() == Sens.ENTREE ? e.getMontant() : e.getMontant().negate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (balance.abs().compareTo(BALANCE_TOLERANCE) > 0) {
            throw new UnbalancedEntryException("Écritures non équilibrées: balance = " + balance
                    + ". Les entrées et sorties doivent s'équilibrer.");
        }

        log.info("Recording double-entry with {} entries, balance check passed: {}", entries.size(), balance);

        entries.forEach(entry -> {
            Caisse caisse = caisseRepository.findById(entry.getCaisseId())
                    .orElseThrow(() -> new EntityNotFoundException("Caisse", entry.getCaisseId().toString()));

            if (caisse.getStatut() != CaisseStatut.ACTIVE) {
                throw new IllegalStateException("La caisse " + caisse.getNumero() + " n'est pas active");
            }

            BigDecimal soldeAvant = calculateBalance(caisse.getId());
            BigDecimal soldeApres;

            if (entry.getSens() == Sens.ENTREE) {
                soldeApres = soldeAvant.add(entry.getMontant());
            } else {
                soldeApres = soldeAvant.subtract(entry.getMontant());
                if (soldeApres.compareTo(BigDecimal.ZERO) < 0) {
                    log.warn("Caisse {} balance will go negative: {} - {} = {}",
                            caisse.getNumero(), soldeAvant, entry.getMontant(), soldeApres);
                }
            }

            MouvementCaisse mouvement = MouvementCaisse.builder()
                    .id(UUID.randomUUID())
                    .caisseId(entry.getCaisseId())
                    .type(entry.getType())
                    .sens(entry.getSens())
                    .montant(entry.getMontant())
                    .soldeAvant(soldeAvant)
                    .soldeApres(soldeApres)
                    .dateOperation(LocalDateTime.now())
                    .description(entry.getDescription())
                    .referenceType(entry.getReferenceType())
                    .referenceId(entry.getReferenceId())
                    .build();

            mouvementCaisseRepository.save(mouvement);
            log.debug("Recorded mouvement: caisse={}, sens={}, montant={}, soldeAvant={}, soldeApres={}",
                    caisse.getNumero(), entry.getSens(), entry.getMontant(), soldeAvant, soldeApres);
        });
    }

    /**
     * Calculate current balance for a caisse.
     * Balance = soldeInitial + totalEntrees - totalSorties
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateBalance(UUID caisseId) {
        Caisse caisse = caisseRepository.findById(caisseId)
                .orElseThrow(() -> new EntityNotFoundException("Caisse", caisseId.toString()));

        BigDecimal totalEntrees = mouvementCaisseRepository.sumByCaisseIdAndSens(caisseId, Sens.ENTREE);
        BigDecimal totalSorties = mouvementCaisseRepository.sumByCaisseIdAndSens(caisseId, Sens.SORTIE);

        return caisse.getSoldeInitial().add(totalEntrees).subtract(totalSorties);
    }

    /**
     * Get detailed balance response for a caisse.
     */
    @Transactional(readOnly = true)
    public CaisseBalanceResponse getCaisseBalance(UUID caisseId) {
        Caisse caisse = caisseRepository.findById(caisseId)
                .orElseThrow(() -> new EntityNotFoundException("Caisse", caisseId.toString()));

        BigDecimal totalEntrees = mouvementCaisseRepository.sumByCaisseIdAndSens(caisseId, Sens.ENTREE);
        BigDecimal totalSorties = mouvementCaisseRepository.sumByCaisseIdAndSens(caisseId, Sens.SORTIE);
        BigDecimal soldeActuel = caisse.getSoldeInitial().add(totalEntrees).subtract(totalSorties);

        boolean lowBalance = caisse.getSeuilAlerte().compareTo(BigDecimal.ZERO) > 0
                && soldeActuel.compareTo(caisse.getSeuilAlerte()) < 0;

        return CaisseBalanceResponse.builder()
                .id(caisse.getId())
                .numero(caisse.getNumero())
                .nom(caisse.getNom())
                .type(caisse.getType())
                .statut(caisse.getStatut())
                .soldeInitial(caisse.getSoldeInitial())
                .totalEntrees(totalEntrees)
                .totalSorties(totalSorties)
                .soldeActuel(soldeActuel)
                .seuilAlerte(caisse.getSeuilAlerte())
                .lowBalance(lowBalance)
                .computedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Get journal de caisse for a date range.
     */
    @Transactional(readOnly = true)
    public JournalCaisseResponse getJournalCaisse(UUID caisseId, LocalDate debut, LocalDate fin) {
        Caisse caisse = caisseRepository.findById(caisseId)
                .orElseThrow(() -> new EntityNotFoundException("Caisse", caisseId.toString()));

        LocalDateTime debutDateTime = debut != null ? debut.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime finDateTime = fin != null ? fin.atTime(LocalTime.MAX) : LocalDateTime.now();

        List<MouvementCaisse> mouvements = mouvementCaisseRepository
                .findByCaisseIdAndDateOperationBetween(caisseId, debutDateTime, finDateTime);

        BigDecimal totalEntrees = mouvementCaisseRepository.sumByCaisseIdAndSensAndDateOperationBetween(
                caisseId, Sens.ENTREE, debutDateTime, finDateTime);
        BigDecimal totalSorties = mouvementCaisseRepository.sumByCaisseIdAndSensAndDateOperationBetween(
                caisseId, Sens.SORTIE, debutDateTime, finDateTime);

        BigDecimal soldeDebut = calculateBalanceAtDate(caisseId, debutDateTime);
        BigDecimal soldeFin = soldeDebut.add(totalEntrees).subtract(totalSorties);

        List<MouvementCaisseResponse> mouvementResponses = mouvementCaisseMapper.toResponseList(mouvements);

        return JournalCaisseResponse.builder()
                .caisseId(caisseId)
                .caisseNumero(caisse.getNumero())
                .caisseNom(caisse.getNom())
                .dateDebut(debut)
                .dateFin(fin)
                .soldeDebut(soldeDebut)
                .mouvements(mouvementResponses)
                .totalEntrees(totalEntrees)
                .totalSorties(totalSorties)
                .soldeFin(soldeFin)
                .build();
    }

    /**
     * Calculate balance at a specific point in time (before a given datetime).
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateBalanceAtDate(UUID caisseId, LocalDateTime dateTime) {
        Caisse caisse = caisseRepository.findById(caisseId)
                .orElseThrow(() -> new EntityNotFoundException("Caisse", caisseId.toString()));

        BigDecimal totalEntrees = mouvementCaisseRepository.sumByCaisseIdAndSensAndDateOperationBetween(
                caisseId, Sens.ENTREE, LocalDateTime.of(2000, 1, 1, 0, 0), dateTime);
        BigDecimal totalSorties = mouvementCaisseRepository.sumByCaisseIdAndSensAndDateOperationBetween(
                caisseId, Sens.SORTIE, LocalDateTime.of(2000, 1, 1, 0, 0), dateTime);

        return caisse.getSoldeInitial().add(totalEntrees).subtract(totalSorties);
    }

    /**
     * Create member caisses (called when member.created event is received).
     * Creates COURANT and EPARGNE caisses for the new member.
     */
    public void createMemberCaisses(UUID membreId) {
        log.info("Creating default caisses for new member: {}", membreId);

        if (caisseRepository.existsByMembreIdAndType(membreId, CaisseType.COURANT)) {
            log.warn("Member {} already has a COURANT caisse, skipping creation", membreId);
            return;
        }

        String membreIdStr = membreId.toString().substring(0, 8).toUpperCase();

        Caisse caisseCourant = Caisse.builder()
                .id(UUID.randomUUID())
                .numero("CAG-COU-" + membreIdStr)
                .nom("Caisse Courante - " + membreIdStr)
                .type(CaisseType.COURANT)
                .statut(CaisseStatut.ACTIVE)
                .soldeInitial(BigDecimal.ZERO)
                .membreId(membreId)
                .seuilAlerte(BigDecimal.ZERO)
                .build();

        Caisse caisseEpargne = Caisse.builder()
                .id(UUID.randomUUID())
                .numero("CAG-EPG-" + membreIdStr)
                .nom("Caisse Épargne - " + membreIdStr)
                .type(CaisseType.EPARGNE)
                .statut(CaisseStatut.ACTIVE)
                .soldeInitial(BigDecimal.ZERO)
                .membreId(membreId)
                .seuilAlerte(BigDecimal.ZERO)
                .build();

        caisseRepository.save(caisseCourant);
        caisseRepository.save(caisseEpargne);

        log.info("Created COURANT and EPARGNE caisses for member {}", membreId);
    }

    /**
     * Verify that the total finance system is balanced.
     * Sum of all ENTREE across all caisses must equal sum of all SORTIE.
     */
    @Transactional(readOnly = true)
    public boolean isSystemBalanced() {
        BigDecimal totalEntrees = mouvementCaisseRepository.sumAllBySens(Sens.ENTREE);
        BigDecimal totalSorties = mouvementCaisseRepository.sumAllBySens(Sens.SORTIE);

        BigDecimal balance = totalEntrees.subtract(totalSorties);
        boolean balanced = balance.abs().compareTo(BALANCE_TOLERANCE) <= 0;

        if (!balanced) {
            log.error("FINANCE SYSTEM UNBALANCED! Total ENTREE={}, Total SORTIE={}, Balance={}",
                    totalEntrees, totalSorties, balance);
        } else {
            log.info("Finance system balanced: Total ENTREE={}, Total SORTIE={}, Balance={}",
                    totalEntrees, totalSorties, balance);
        }

        return balanced;
    }

    /**
     * Get all caisses with low balance (below seuilAlerte).
     */
    @Transactional(readOnly = true)
    public List<Caisse> findLowBalanceCaisses() {
        return caisseRepository.findByStatut(CaisseStatut.ACTIVE).stream()
                .filter(caisse -> {
                    BigDecimal soldeActuel = calculateBalance(caisse.getId());
                    return caisse.getSeuilAlerte().compareTo(BigDecimal.ZERO) > 0
                            && soldeActuel.compareTo(caisse.getSeuilAlerte()) < 0;
                })
                .toList();
    }
}
