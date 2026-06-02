package com.serenity.account.event;

import com.serenity.account.dto.AccountingEntryRequest;
import com.serenity.account.entity.Caisse;
import com.serenity.account.entity.enums.CaisseStatut;
import com.serenity.account.entity.enums.CaisseType;
import com.serenity.account.entity.enums.MouvementType;
import com.serenity.account.entity.enums.Sens;
import com.serenity.account.repository.CaisseRepository;
import com.serenity.account.service.FinanceService;
import com.serenity.common.event.CollectorSessionClosedEvent;
import com.serenity.common.event.EpargneSubscribedEvent;
import com.serenity.common.event.MemberCreatedEvent;
import com.serenity.common.event.NanoCreditDisbursedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountEventConsumer {

    private static final UUID SYS_CAG_PUB_ID = UUID.fromString("a0000000-0000-0000-0000-000000000001");
    private static final UUID SYS_CAG_PRV_ID = UUID.fromString("a0000000-0000-0000-0000-000000000002");
    private static final UUID SYS_COT_PUB_ID = UUID.fromString("a0000000-0000-0000-0000-000000000003");
    private static final UUID SYS_COT_PRV_ID = UUID.fromString("a0000000-0000-0000-0000-000000000004");
    private static final UUID SYS_NC_CREDIT_ID = UUID.fromString("a0000000-0000-0000-0000-000000000005");
    private static final UUID SYS_NC_IMPAYE_ID = UUID.fromString("a0000000-0000-0000-0000-000000000006");
    private static final UUID SYS_EPS_TONTINE_ID = UUID.fromString("a0000000-0000-0000-0000-000000000007");

    private final FinanceService financeService;
    private final CaisseRepository caisseRepository;

    @KafkaListener(topics = "member.created", groupId = "account-service")
    public void handleMemberCreated(MemberCreatedEvent event) {
        try {
            log.info("Received MemberCreatedEvent for member {} ({})", event.getMemberId(), event.getNumero());
            financeService.createMemberCaisses(event.getMemberId());
            log.info("Successfully created default caisses for member {}", event.getMemberId());
        } catch (Exception e) {
            log.error("Failed to handle MemberCreatedEvent for member {}: {}", event.getMemberId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "nano-credit.disbursed", groupId = "account-service")
    public void handleNanoCreditDisbursed(NanoCreditDisbursedEvent event) {
        try {
            log.info("Received NanoCreditDisbursedEvent for member {}, amount={}", event.getMembreId(), event.getMontant());

            Caisse memberCaisseCourant = caisseRepository
                    .findByMembreIdAndType(event.getMembreId(), CaisseType.COURANT)
                    .stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No COURANT caisse found for member " + event.getMembreId()));

            List<AccountingEntryRequest> entries = new ArrayList<>();

            entries.add(AccountingEntryRequest.builder()
                    .caisseId(SYS_NC_CREDIT_ID)
                    .montant(event.getMontant())
                    .sens(Sens.SORTIE)
                    .type(MouvementType.DEBOURS_NANO_CREDIT)
                    .description("Débours nano-crédit pour membre " + event.getMembreId())
                    .referenceType("NANO_CREDIT")
                    .referenceId(event.getNanoCreditId())
                    .build());

            entries.add(AccountingEntryRequest.builder()
                    .caisseId(memberCaisseCourant.getId())
                    .montant(event.getMontant())
                    .sens(Sens.ENTREE)
                    .type(MouvementType.DEBOURS_NANO_CREDIT)
                    .description("Réception nano-crédit pour membre " + event.getMembreId())
                    .referenceType("NANO_CREDIT")
                    .referenceId(event.getNanoCreditId())
                    .build());

            financeService.recordDoubleEntry(entries);
            log.info("Recorded nano-credit disbursement: {} from credit caisse to member caisse",
                    event.getMontant());
        } catch (Exception e) {
            log.error("Failed to handle NanoCreditDisbursedEvent for member {}: {}",
                    event.getMembreId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "epargne.subscribed", groupId = "account-service")
    public void handleEpargneSubscribed(EpargneSubscribedEvent event) {
        try {
            log.info("Received EpargneSubscribedEvent for member {}, plan {}",
                    event.getMembreId(), event.getPlanId());

            boolean existingTontine = caisseRepository
                    .findByMembreIdAndType(event.getMembreId(), CaisseType.TONTINE)
                    .stream()
                    .anyMatch(c -> c.getStatut() == CaisseStatut.ACTIVE);

            if (existingTontine) {
                log.info("Member {} already has an active TONTINE caisse, skipping creation",
                        event.getMembreId());
                return;
            }

            String membreIdStr = event.getMembreId().toString().substring(0, 8).toUpperCase();

            Caisse tontineCaisse = Caisse.builder()
                    .id(UUID.randomUUID())
                    .numero("TON-" + membreIdStr + "-" + event.getSouscriptionId().toString().substring(0, 4).toUpperCase())
                    .nom("Caisse Tontine - " + membreIdStr)
                    .type(CaisseType.TONTINE)
                    .statut(CaisseStatut.ACTIVE)
                    .soldeInitial(BigDecimal.ZERO)
                    .membreId(event.getMembreId())
                    .seuilAlerte(BigDecimal.ZERO)
                    .build();

            caisseRepository.save(tontineCaisse);
            log.info("Created TONTINE caisse for member {} under souscription {}",
                    event.getMembreId(), event.getSouscriptionId());
        } catch (Exception e) {
            log.error("Failed to handle EpargneSubscribedEvent for member {}: {}",
                    event.getMembreId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = "collector.session.closed", groupId = "account-service")
    public void handleCollectorSessionClosed(CollectorSessionClosedEvent event) {
        try {
            log.info("Received CollectorSessionClosedEvent for session {}, amount={}",
                    event.getSessionId(), event.getMontantTotal());

            List<AccountingEntryRequest> entries = new ArrayList<>();

            entries.add(AccountingEntryRequest.builder()
                    .caisseId(SYS_COT_PUB_ID)
                    .montant(event.getMontantTotal())
                    .sens(Sens.ENTREE)
                    .type(MouvementType.COLLECTE)
                    .description("Collecte session " + event.getSessionId() + " par collecteur " + event.getUserId())
                    .referenceType("COLLECTOR_SESSION")
                    .referenceId(event.getSessionId())
                    .build());

            entries.add(AccountingEntryRequest.builder()
                    .caisseId(SYS_COT_PRV_ID)
                    .montant(event.getMontantTotal())
                    .sens(Sens.SORTIE)
                    .type(MouvementType.COLLECTE)
                    .description("Contrepartie collecte session " + event.getSessionId())
                    .referenceType("COLLECTOR_SESSION")
                    .referenceId(event.getSessionId())
                    .build());

            financeService.recordDoubleEntry(entries);
            log.info("Reconciled collector session {}: amount={}", event.getSessionId(), event.getMontantTotal());
        } catch (Exception e) {
            log.error("Failed to handle CollectorSessionClosedEvent for session {}: {}",
                    event.getSessionId(), e.getMessage(), e);
        }
    }
}
