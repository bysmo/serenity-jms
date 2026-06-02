package com.bysmo.serenity.epargne.service;

import com.bysmo.serenity.epargne.entity.EpargneEcheance;
import com.bysmo.serenity.epargne.entity.EpargneSouscription;
import com.bysmo.serenity.epargne.enums.EcheanceStatut;
import com.bysmo.serenity.epargne.enums.EpargneFrequence;
import com.bysmo.serenity.epargne.repository.EpargneEcheanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EpargneEcheanceService {

    private final EpargneEcheanceRepository echeanceRepository;

    @Transactional
    public List<EpargneEcheance> generateEcheances(EpargneSouscription souscription) {
        log.info("Generating echeances for souscription: {}", souscription.getId());

        EpargneFrequence frequence = souscription.getPlan().getFrequence();
        Integer dureeMois = souscription.getPlan().getDureeMois();
        BigDecimal montantEcheance = souscription.getMontant();

        List<EpargneEcheance> echeances = new ArrayList<>();

        switch (frequence) {
            case MENSUELLE:
                echeances = generateMensuelleEcheances(souscription, dureeMois, montantEcheance);
                break;
            case HEBDOMADAIRE:
                echeances = generateHebdomadaireEcheances(souscription, dureeMois, montantEcheance);
                break;
            case QUOTIDIENNE:
                echeances = generateQuotidienneEcheances(souscription, dureeMois, montantEcheance);
                break;
            case LIBRE:
                echeances = generateLibreEcheances(souscription, montantEcheance);
                break;
            default:
                throw new RuntimeException("Fréquence non supportée: " + frequence);
        }

        echeances = echeanceRepository.saveAll(echeances);
        log.info("Generated {} echeances for souscription {}", echeances.size(), souscription.getId());
        return echeances;
    }

    private List<EpargneEcheance> generateMensuelleEcheances(EpargneSouscription souscription,
                                                               Integer dureeMois, BigDecimal montant) {
        List<EpargneEcheance> echeances = new ArrayList<>();
        int nombreEcheances = dureeMois != null ? dureeMois : 12;
        LocalDate startDate = LocalDate.now();

        for (int i = 1; i <= nombreEcheances; i++) {
            EpargneEcheance echeance = new EpargneEcheance();
            echeance.setSouscription(souscription);
            echeance.setNumeroEcheance(i);
            echeance.setMontant(montant);
            echeance.setDateEcheance(startDate.plusMonths(i));
            echeance.setStatut(EcheanceStatut.EN_ATTENTE);
            echeance.setMontantPaye(BigDecimal.ZERO);
            echeance.setMontantPenalite(BigDecimal.ZERO);
            echeances.add(echeance);
        }

        return echeances;
    }

    private List<EpargneEcheance> generateHebdomadaireEcheances(EpargneSouscription souscription,
                                                                  Integer dureeMois, BigDecimal montant) {
        List<EpargneEcheance> echeances = new ArrayList<>();
        int nombreEcheances = dureeMois != null ? dureeMois * 4 : 48;
        // Start from next Monday
        LocalDate startDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.MONDAY));

        for (int i = 1; i <= nombreEcheances; i++) {
            EpargneEcheance echeance = new EpargneEcheance();
            echeance.setSouscription(souscription);
            echeance.setNumeroEcheance(i);
            echeance.setMontant(montant);
            echeance.setDateEcheance(startDate.plusWeeks(i - 1));
            echeance.setStatut(EcheanceStatut.EN_ATTENTE);
            echeance.setMontantPaye(BigDecimal.ZERO);
            echeance.setMontantPenalite(BigDecimal.ZERO);
            echeances.add(echeance);
        }

        return echeances;
    }

    private List<EpargneEcheance> generateQuotidienneEcheances(EpargneSouscription souscription,
                                                                 Integer dureeMois, BigDecimal montant) {
        List<EpargneEcheance> echeances = new ArrayList<>();
        int nombreEcheances = dureeMois != null ? dureeMois * 30 : 360;
        LocalDate startDate = LocalDate.now().plusDays(1);

        for (int i = 1; i <= nombreEcheances; i++) {
            EpargneEcheance echeance = new EpargneEcheance();
            echeance.setSouscription(souscription);
            echeance.setNumeroEcheance(i);
            echeance.setMontant(montant);
            echeance.setDateEcheance(startDate.plusDays(i - 1));
            echeance.setStatut(EcheanceStatut.EN_ATTENTE);
            echeance.setMontantPaye(BigDecimal.ZERO);
            echeance.setMontantPenalite(BigDecimal.ZERO);
            echeances.add(echeance);
        }

        return echeances;
    }

    private List<EpargneEcheance> generateLibreEcheances(EpargneSouscription souscription,
                                                           BigDecimal montant) {
        List<EpargneEcheance> echeances = new ArrayList<>();
        // For LIBRE frequency, create a single open-ended echeance
        // The member can pay at any time without a fixed schedule
        EpargneEcheance echeance = new EpargneEcheance();
        echeance.setSouscription(souscription);
        echeance.setNumeroEcheance(1);
        echeance.setMontant(montant);
        echeance.setDateEcheance(LocalDate.now().plusMonths(1));
        echeance.setStatut(EcheanceStatut.EN_ATTENTE);
        echeance.setMontantPaye(BigDecimal.ZERO);
        echeance.setMontantPenalite(BigDecimal.ZERO);
        echeances.add(echeance);

        return echeances;
    }

    @Transactional(readOnly = true)
    public List<EpargneEcheance> getBySouscription(UUID souscriptionId) {
        log.debug("Fetching echeances for souscription: {}", souscriptionId);
        return echeanceRepository.findBySouscriptionIdOrderByNumeroEcheanceAsc(souscriptionId);
    }

    @Transactional(readOnly = true)
    public List<EpargneEcheance> getUpcomingEcheances(UUID membreId, int days) {
        log.debug("Fetching upcoming echeances for membre {} in next {} days", membreId, days);
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);

        return echeanceRepository.findUpcomingEcheancesByMembre(
                membreId,
                today,
                endDate,
                List.of(EcheanceStatut.EN_ATTENTE, EcheanceStatut.PARTIELLEMENT_PAYEE)
        );
    }

    @Transactional(readOnly = true)
    public List<EpargneEcheance> getEcheancesDueBetween(LocalDate startDate, LocalDate endDate) {
        return echeanceRepository.findEcheancesDueBetween(
                startDate,
                endDate,
                List.of(EcheanceStatut.EN_ATTENTE, EcheanceStatut.PARTIELLEMENT_PAYEE)
        );
    }

    @Transactional(readOnly = true)
    public List<EpargneEcheance> getOverdueEcheances() {
        LocalDate today = LocalDate.now();
        return echeanceRepository.findOverdueEcheances(EcheanceStatut.EN_ATTENTE, today);
    }

    @Transactional
    public EpargneEcheance updateStatut(UUID echeanceId, EcheanceStatut statut) {
        log.info("Updating echeance {} statut to {}", echeanceId, statut);

        EpargneEcheance echeance = echeanceRepository.findById(echeanceId)
                .orElseThrow(() -> new RuntimeException("Échéance non trouvée avec l'id: " + echeanceId));

        echeance.setStatut(statut);
        return echeanceRepository.save(echeance);
    }

    @Transactional
    public EpargneEcheance getById(UUID id) {
        return echeanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Échéance non trouvée avec l'id: " + id));
    }

    @Transactional
    public void markOverdueEcheances() {
        log.info("Marking overdue echeances as EN_RETARD");

        List<EpargneEcheance> overdueEcheances = getOverdueEcheances();
        for (EpargneEcheance echeance : overdueEcheances) {
            echeance.setStatut(EcheanceStatut.EN_RETARD);
            echeanceRepository.save(echeance);
            log.debug("Echeance {} marked as EN_RETARD", echeance.getId());
        }

        log.info("Marked {} echeances as EN_RETARD", overdueEcheances.size());
    }

    @Transactional
    public EpargneEcheance updateEcheanceOnVersement(UUID echeanceId, BigDecimal montantVerse) {
        EpargneEcheance echeance = getById(echeanceId);

        BigDecimal nouveauMontantPaye = echeance.getMontantPaye().add(montantVerse);
        echeance.setMontantPaye(nouveauMontantPaye);
        echeance.setDatePaiement(java.time.LocalDateTime.now());

        // Update statut based on payment
        int comparison = nouveauMontantPaye.compareTo(echeance.getMontant());
        if (comparison >= 0) {
            echeance.setStatut(EcheanceStatut.PAYEE);
        } else if (nouveauMontantPaye.compareTo(BigDecimal.ZERO) > 0) {
            echeance.setStatut(EcheanceStatut.PARTIELLEMENT_PAYEE);
        }

        return echeanceRepository.save(echeance);
    }
}
