package com.bysmo.serenity.nanocredit.service;

import com.bysmo.serenity.nanocredit.entity.NanoCredit;
import com.bysmo.serenity.nanocredit.entity.NanoCreditEcheance;
import com.bysmo.serenity.nanocredit.entity.NanoCreditPalier;
import com.bysmo.serenity.nanocredit.entity.enums.EcheanceStatut;
import com.bysmo.serenity.nanocredit.entity.enums.FrequenceRemboursement;
import com.bysmo.serenity.nanocredit.repository.NanoCreditEcheanceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmortizationService {

    private final NanoCreditEcheanceRepository echeanceRepository;

    private static final MathContext MC = new MathContext(20, RoundingMode.HALF_UP);
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * Generates the full amortization schedule for a nano-credit.
     * Uses flat interest rate calculation with equal installments based on palier frequency.
     *
     * @param credit the nano-credit
     * @return list of generated echeances
     */
    @Transactional
    public List<NanoCreditEcheance> generateEcheances(NanoCredit credit) {
        NanoCreditPalier palier = credit.getPalier();
        FrequenceRemboursement frequence = palier.getFrequenceRemboursement();
        int dureeJours = palier.getDureeJours();
        BigDecimal montant = credit.getMontant();
        BigDecimal tauxInteret = palier.getTauxInteret();

        log.info("Generating echeances for creditId={}, montant={}, tauxInteret={}, frequence={}, dureeJours={}",
                credit.getId(), montant, tauxInteret, frequence, dureeJours);

        // Calculate number of installments based on frequency and duration
        int nombreEcheances = calculateNombreEcheances(frequence, dureeJours);

        // Calculate total interest (flat rate)
        BigDecimal interetTotal = montant.multiply(tauxInteret, MC)
                .divide(ONE_HUNDRED, MC)
                .setScale(4, RoundingMode.HALF_UP);

        // Calculate total amount to repay
        BigDecimal montantTotalRembourse = montant.add(interetTotal);

        // Calculate installment amount (equal payments)
        BigDecimal montantEcheance = montantTotalRembourse.divide(
                BigDecimal.valueOf(nombreEcheances), 4, RoundingMode.HALF_UP);

        // Generate schedule
        LocalDate startDate = LocalDate.now();
        List<NanoCreditEcheance> echeances = new ArrayList<>();

        BigDecimal montantRestant = montantTotalRembourse;

        for (int i = 1; i <= nombreEcheances; i++) {
            LocalDate dateEcheance = calculateDateEcheance(startDate, frequence, i);

            BigDecimal montantPourCetteEcheance;
            if (i == nombreEcheances) {
                // Last installment: adjust for rounding
                montantPourCetteEcheance = montantRestant;
            } else {
                montantPourCetteEcheance = montantEcheance;
                montantRestant = montantRestant.subtract(montantEcheance);
            }

            NanoCreditEcheance echeance = NanoCreditEcheance.builder()
                    .nanoCredit(credit)
                    .numeroEcheance(i)
                    .montant(montantPourCetteEcheance)
                    .montantPenalite(BigDecimal.ZERO)
                    .dateEcheance(dateEcheance)
                    .statut(EcheanceStatut.EN_ATTENTE)
                    .montantPaye(BigDecimal.ZERO)
                    .build();

            echeances.add(echeance);
        }

        List<NanoCreditEcheance> saved = echeanceRepository.saveAll(echeances);

        log.info("Generated {} echeances for creditId={}, montantEcheance={}, montantTotal={}",
                saved.size(), credit.getId(), montantEcheance, montantTotalRembourse);

        return saved;
    }

    /**
     * Calculates the number of installments based on frequency and total duration.
     */
    private int calculateNombreEcheances(FrequenceRemboursement frequence, int dureeJours) {
        return switch (frequence) {
            case QUOTIDIENNE -> dureeJours; // One payment per day
            case HEBDOMADAIRE -> Math.max(1, dureeJours / 7); // One payment per week
            case MENSUELLE -> Math.max(1, dureeJours / 30); // One payment per month
        };
    }

    /**
     * Calculates the due date for each installment based on frequency and installment number.
     */
    private LocalDate calculateDateEcheance(LocalDate startDate, FrequenceRemboursement frequence, int numeroEcheance) {
        return switch (frequence) {
            case QUOTIDIENNE -> startDate.plusDays(numeroEcheance);
            case HEBDOMADAIRE -> startDate.plusWeeks(numeroEcheance);
            case MENSUELLE -> startDate.plusMonths(numeroEcheance);
        };
    }

    /**
     * Calculates the total interest for a credit.
     */
    public BigDecimal calculerInteretTotal(BigDecimal montant, BigDecimal tauxInteret) {
        return montant.multiply(tauxInteret, MC)
                .divide(ONE_HUNDRED, MC)
                .setScale(4, RoundingMode.HALF_UP);
    }

    /**
     * Calculates the total repayment amount (principal + interest).
     */
    public BigDecimal calculerMontantTotalRembourse(BigDecimal montant, BigDecimal tauxInteret) {
        return montant.add(calculerInteretTotal(montant, tauxInteret));
    }

    /**
     * Calculates the installment amount per payment period.
     */
    public BigDecimal calculerMontantEcheance(BigDecimal montant, BigDecimal tauxInteret, FrequenceRemboursement frequence, int dureeJours) {
        int nombreEcheances = calculateNombreEcheances(frequence, dureeJours);
        BigDecimal montantTotal = calculerMontantTotalRembourse(montant, tauxInteret);
        return montantTotal.divide(BigDecimal.valueOf(nombreEcheances), 4, RoundingMode.HALF_UP);
    }
}
