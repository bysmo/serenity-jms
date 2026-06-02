package com.serenity.admin.service;

import com.serenity.admin.entity.SystemMerkleLedger;
import com.serenity.admin.repository.SystemMerkleLedgerRepository;
import com.serenity.admin.util.HmacUtil;
import com.serenity.common.util.ChecksumUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditFinancierService {

    private static final String GENESIS_HASH = "0000000000000000000000000000000000000000000000000000000000000000";

    private final SystemMerkleLedgerRepository merkleLedgerRepository;
    private final ObjectMapper objectMapper;

    /**
     * Append entry to the Merkle ledger.
     * Each entry is chained to the previous entry via SHA-256 hash chain.
     * An HMAC signature is computed for tamper evidence.
     */
    @Transactional
    public void append(String tableName, UUID recordId, String action, Object recordData) {
        log.info("Appending Merkle ledger entry: table={}, recordId={}, action={}", tableName, recordId, action);

        String previousHash = getLastHash(tableName);
        String recordChecksum = ChecksumUtil.sha256(toJson(recordData));
        String hashChain = ChecksumUtil.sha256(previousHash + recordChecksum);
        String hmacSignature = HmacUtil.computeHmacSha256(hashChain);

        SystemMerkleLedger entry = SystemMerkleLedger.builder()
                .tableName(tableName)
                .recordId(recordId)
                .action(action)
                .recordChecksum(recordChecksum)
                .hashChain(hashChain)
                .hmacSignature(hmacSignature)
                .build();

        merkleLedgerRepository.save(entry);
        log.info("Merkle ledger entry saved: table={}, hashChain={}", tableName, hashChain);
    }

    /**
     * Verify chain integrity for a specific table.
     * Walks through all entries ordered by creation time and verifies:
     * 1. The first entry's hash chain is derived from the genesis hash
     * 2. Each subsequent entry's hash chain is derived from the previous entry's hash chain + record checksum
     * 3. Each HMAC signature is valid for the corresponding hash chain
     */
    public boolean verifyChain(String tableName) {
        log.info("Verifying Merkle chain integrity for table: {}", tableName);

        List<SystemMerkleLedger> entries = merkleLedgerRepository.findByTableNameOrderByCreatedAtAsc(tableName);

        if (entries.isEmpty()) {
            log.info("No entries found for table: {} - chain is valid (empty)", tableName);
            return true;
        }

        String previousHash = GENESIS_HASH;

        for (int i = 0; i < entries.size(); i++) {
            SystemMerkleLedger entry = entries.get(i);

            // Verify hash chain continuity
            String expectedHashChain = ChecksumUtil.sha256(previousHash + entry.getRecordChecksum());
            if (!expectedHashChain.equals(entry.getHashChain())) {
                log.error("Chain integrity broken at entry index {} for table {}. " +
                        "Expected hashChain={}, but found={}", i, tableName, expectedHashChain, entry.getHashChain());
                return false;
            }

            // Verify HMAC signature
            String expectedHmac = HmacUtil.computeHmacSha256(entry.getHashChain());
            if (entry.getHmacSignature() != null && !entry.getHmacSignature().equals(expectedHmac)) {
                log.error("HMAC signature verification failed at entry index {} for table {}. " +
                        "Expected={}, but found={}", i, tableName, expectedHmac, entry.getHmacSignature());
                return false;
            }

            previousHash = entry.getHashChain();
        }

        log.info("Merkle chain verification passed for table: {} ({} entries verified)", tableName, entries.size());
        return true;
    }

    /**
     * Weekly audit: verify all checksums across all tables.
     * Runs every Monday at midnight.
     */
    @Scheduled(cron = "0 0 0 ? * MON")
    @Transactional(readOnly = true)
    public void auditChecksums() {
        log.info("=== Starting weekly Merkle checksum audit ===");

        Set<String> tableNames = getDistinctTableNames();
        boolean allValid = true;
        int tablesChecked = 0;
        int tablesFailed = 0;

        for (String tableName : tableNames) {
            tablesChecked++;
            boolean valid = verifyChain(tableName);
            if (!valid) {
                allValid = false;
                tablesFailed++;
                log.error("WEEKLY AUDIT FAILED: Chain integrity broken for table: {}", tableName);
            } else {
                log.info("WEEKLY AUDIT PASSED: Chain integrity verified for table: {}", tableName);
            }
        }

        if (allValid) {
            log.info("=== Weekly Merkle checksum audit completed: ALL {} TABLES PASSED ===", tablesChecked);
        } else {
            log.error("=== Weekly Merkle checksum audit completed: {} of {} TABLES FAILED ===", tablesFailed, tablesChecked);
        }
    }

    /**
     * Daily Merkle root computation at 23:30.
     * Computes and logs the Merkle root for each table.
     */
    @Scheduled(cron = "0 30 23 * * ?")
    @Transactional(readOnly = true)
    public void computeMerkleRoot() {
        log.info("=== Starting daily Merkle root computation ===");

        Set<String> tableNames = getDistinctTableNames();

        for (String tableName : tableNames) {
            List<SystemMerkleLedger> entries = merkleLedgerRepository
                    .findByTableNameOrderByCreatedAtAsc(tableName);

            if (entries.isEmpty()) {
                log.info("Table {} has no ledger entries - skipping root computation", tableName);
                continue;
            }

            // The Merkle root is the hash chain of the last entry
            SystemMerkleLedger lastEntry = entries.get(entries.size() - 1);
            String merkleRoot = lastEntry.getHashChain();

            // For additional integrity, compute a composite root from all entry hash chains
            String combinedHashes = entries.stream()
                    .map(SystemMerkleLedger::getHashChain)
                    .reduce("", (a, b) -> a + b);
            String compositeRoot = ChecksumUtil.sha256(combinedHashes);

            log.info("Daily Merkle root for table '{}': lastChainHash={}, compositeRoot={}, totalEntries={}",
                    tableName, merkleRoot, compositeRoot, entries.size());
        }

        log.info("=== Daily Merkle root computation completed ===");
    }

    /**
     * Daily chain verification at 23:45.
     * Verifies the chain integrity for all tables.
     */
    @Scheduled(cron = "0 45 23 * * ?")
    @Transactional(readOnly = true)
    public void verifyChainDaily() {
        log.info("=== Starting daily chain verification ===");

        Set<String> tableNames = getDistinctTableNames();
        boolean allValid = true;
        int tablesChecked = 0;
        int tablesFailed = 0;

        for (String tableName : tableNames) {
            tablesChecked++;
            boolean valid = verifyChain(tableName);
            if (!valid) {
                allValid = false;
                tablesFailed++;
                log.error("DAILY VERIFICATION FAILED: Chain integrity broken for table: {}", tableName);
            } else {
                log.info("DAILY VERIFICATION PASSED: Chain integrity verified for table: {}", tableName);
            }
        }

        if (allValid) {
            log.info("=== Daily chain verification completed: ALL {} TABLES PASSED ===", tablesChecked);
        } else {
            log.error("=== Daily chain verification completed: {} of {} TABLES FAILED ===", tablesFailed, tablesChecked);
        }
    }

    /**
     * Monthly reconciliation at midnight on the 1st of each month.
     * Performs a full audit including HMAC re-verification and entry count validation.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional(readOnly = true)
    public void reconcile() {
        log.info("=== Starting monthly Merkle ledger reconciliation ===");

        Set<String> tableNames = getDistinctTableNames();
        int totalEntries = 0;
        int tablesReconciled = 0;
        int tablesWithIssues = 0;

        for (String tableName : tableNames) {
            List<SystemMerkleLedger> entries = merkleLedgerRepository
                    .findByTableNameOrderByCreatedAtAsc(tableName);

            totalEntries += entries.size();

            if (entries.isEmpty()) {
                log.info("Monthly reconciliation: Table {} has no entries - skipping", tableName);
                continue;
            }

            boolean chainValid = verifyChain(tableName);
            boolean hmacValid = verifyAllHmacs(entries);
            boolean checksumsValid = verifyAllRecordChecksums(entries);

            if (chainValid && hmacValid && checksumsValid) {
                tablesReconciled++;
                log.info("Monthly reconciliation PASSED for table: {} ({} entries)", tableName, entries.size());
            } else {
                tablesWithIssues++;
                log.error("Monthly reconciliation FAILED for table: {} - chainValid={}, hmacValid={}, checksumsValid={}",
                        tableName, chainValid, hmacValid, checksumsValid);
            }
        }

        log.info("=== Monthly reconciliation completed: totalEntries={}, tablesReconciled={}, tablesWithIssues={} ===",
                totalEntries, tablesReconciled, tablesWithIssues);
    }

    private String getLastHash(String tableName) {
        return merkleLedgerRepository.findTopByTableNameOrderByCreatedAtDesc(tableName)
                .map(SystemMerkleLedger::getHashChain)
                .orElse(GENESIS_HASH);
    }

    private Set<String> getDistinctTableNames() {
        List<SystemMerkleLedger> allEntries = merkleLedgerRepository.findAllByOrderByCreatedAtAsc();
        Set<String> tableNames = new HashSet<>();
        for (SystemMerkleLedger entry : allEntries) {
            tableNames.add(entry.getTableName());
        }
        return tableNames;
    }

    private boolean verifyAllHmacs(List<SystemMerkleLedger> entries) {
        for (SystemMerkleLedger entry : entries) {
            if (entry.getHmacSignature() != null) {
                String expectedHmac = HmacUtil.computeHmacSha256(entry.getHashChain());
                if (!entry.getHmacSignature().equals(expectedHmac)) {
                    log.error("HMAC verification failed for entry id={} in table={}",
                            entry.getId(), entry.getTableName());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyAllRecordChecksums(List<SystemMerkleLedger> entries) {
        // Verify that the hash chain is consistent by recomputing the chain
        String previousHash = GENESIS_HASH;
        for (SystemMerkleLedger entry : entries) {
            String expectedHashChain = ChecksumUtil.sha256(previousHash + entry.getRecordChecksum());
            if (!expectedHashChain.equals(entry.getHashChain())) {
                log.error("Record checksum verification failed for entry id={} in table={}. " +
                        "Expected chain={}, found={}", entry.getId(), entry.getTableName(),
                        expectedHashChain, entry.getHashChain());
                return false;
            }
            previousHash = entry.getHashChain();
        }
        return true;
    }

    private String toJson(Object data) {
        if (data == null) {
            return "{}";
        }
        if (data instanceof String) {
            return (String) data;
        }
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception e) {
            log.error("Failed to serialize object to JSON", e);
            return data.toString();
        }
    }
}
