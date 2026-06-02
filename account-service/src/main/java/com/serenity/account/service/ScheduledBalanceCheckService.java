package com.serenity.account.service;

import com.serenity.account.entity.Caisse;
import com.serenity.account.event.AccountEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ScheduledBalanceCheckService {

    private final FinanceService financeService;
    private final AccountEventPublisher accountEventPublisher;

    /**
     * Check low balances daily at 9h.
     * Identifies caisses with balance below seuilAlerte and publishes events.
     */
    @Scheduled(cron = "0 0 9 * * ?")
    public void checkLowBalances() {
        log.info("=== Starting daily low balance check ===");
        try {
            List<Caisse> lowBalanceCaisses = financeService.findLowBalanceCaisses();

            if (lowBalanceCaisses.isEmpty()) {
                log.info("No low balance caisses detected");
            } else {
                log.warn("Found {} caisses with low balance", lowBalanceCaisses.size());
                for (Caisse caisse : lowBalanceCaisses) {
                    BigDecimal soldeActuel = financeService.calculateBalance(caisse.getId());
                    accountEventPublisher.publishLowBalance(
                            caisse.getId(),
                            caisse.getNumero(),
                            soldeActuel,
                            caisse.getSeuilAlerte()
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error during low balance check: {}", e.getMessage(), e);
        }
        log.info("=== Completed daily low balance check ===");
    }

    /**
     * Check finance balance daily at 23h.
     * Verifies that the total system is balanced (sum of all ENTREE == sum of all SORTIE).
     */
    @Scheduled(cron = "0 0 23 * * ?")
    public void checkFinanceBalance() {
        log.info("=== Starting daily finance balance verification ===");
        try {
            boolean isBalanced = financeService.isSystemBalanced();
            if (isBalanced) {
                log.info("Finance system is balanced");
            } else {
                log.error("ALERT: Finance system is UNBALANCED! Immediate investigation required.");
            }
        } catch (Exception e) {
            log.error("Error during finance balance check: {}", e.getMessage(), e);
        }
        log.info("=== Completed daily finance balance verification ===");
    }
}
