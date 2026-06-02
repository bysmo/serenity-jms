package com.serenity.account.service;

import com.serenity.account.dto.AccountingEntryRequest;
import com.serenity.account.dto.ApprovisionnementRequest;
import com.serenity.account.dto.ApprovisionnementResponse;
import com.serenity.account.dto.CaisseBalanceResponse;
import com.serenity.account.dto.CaisseRequest;
import com.serenity.account.dto.CaisseResponse;
import com.serenity.account.dto.JournalCaisseResponse;
import com.serenity.account.dto.SortieCaisseRequest;
import com.serenity.account.dto.SortieCaisseResponse;
import com.serenity.account.dto.TransfertRequest;
import com.serenity.account.dto.TransfertResponse;
import com.serenity.account.entity.Approvisionnement;
import com.serenity.account.entity.Caisse;
import com.serenity.account.entity.SortieCaisse;
import com.serenity.account.entity.Transfert;
import com.serenity.account.entity.enums.CaisseStatut;
import com.serenity.account.entity.enums.CaisseType;
import com.serenity.account.entity.enums.MouvementType;
import com.serenity.account.entity.enums.Sens;
import com.serenity.account.mapper.ApprovisionnementMapper;
import com.serenity.account.mapper.CaisseMapper;
import com.serenity.account.mapper.SortieCaisseMapper;
import com.serenity.account.mapper.TransfertMapper;
import com.serenity.account.repository.ApprovisionnementRepository;
import com.serenity.account.repository.CaisseRepository;
import com.serenity.account.repository.SortieCaisseRepository;
import com.serenity.account.repository.TransfertRepository;
import com.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class CaisseService {

    private final CaisseRepository caisseRepository;
    private final TransfertRepository transfertRepository;
    private final ApprovisionnementRepository approvisionnementRepository;
    private final SortieCaisseRepository sortieCaisseRepository;
    private final FinanceService financeService;
    private final CaisseMapper caisseMapper;
    private final TransfertMapper transfertMapper;
    private final ApprovisionnementMapper approvisionnementMapper;
    private final SortieCaisseMapper sortieCaisseMapper;

    @Transactional(readOnly = true)
    public List<CaisseResponse> listCaisses(CaisseType type, CaisseStatut statut, UUID membreId) {
        List<Caisse> caisses;
        if (type != null && statut != null) {
            caisses = caisseRepository.findByTypeAndStatut(type, statut);
        } else if (type != null) {
            caisses = caisseRepository.findByType(type);
        } else if (statut != null) {
            caisses = caisseRepository.findByStatut(statut);
        } else if (membreId != null) {
            caisses = caisseRepository.findByMembreId(membreId);
        } else {
            caisses = caisseRepository.findAll();
        }
        return caisseMapper.toResponseList(caisses);
    }

    @Transactional(readOnly = true)
    public CaisseResponse getCaisse(UUID id) {
        Caisse caisse = findCaisseOrThrow(id);
        return caisseMapper.toResponse(caisse);
    }

    @Transactional(readOnly = true)
    public CaisseBalanceResponse getCaisseWithBalance(UUID id) {
        return financeService.getCaisseBalance(id);
    }

    public CaisseResponse createCaisse(CaisseRequest request) {
        Caisse caisse = caisseMapper.toEntity(request);
        caisse.setId(UUID.randomUUID());

        String prefix = getCaissePrefix(request.getType());
        String numero = prefix + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        caisse.setNumero(numero);

        if (request.getStatut() == null) {
            caisse.setStatut(CaisseStatut.ACTIVE);
        }
        if (caisse.getSoldeInitial() == null) {
            caisse.setSoldeInitial(BigDecimal.ZERO);
        }
        if (caisse.getSeuilAlerte() == null) {
            caisse.setSeuilAlerte(BigDecimal.ZERO);
        }

        Caisse saved = caisseRepository.save(caisse);
        log.info("Created caisse: {} ({})", saved.getNumero(), saved.getId());
        return caisseMapper.toResponse(saved);
    }

    public CaisseResponse updateCaisse(UUID id, CaisseRequest request) {
        Caisse caisse = findCaisseOrThrow(id);
        caisseMapper.updateEntityFromRequest(request, caisse);
        Caisse saved = caisseRepository.save(caisse);
        log.info("Updated caisse: {} ({})", saved.getNumero(), saved.getId());
        return caisseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CaisseBalanceResponse getBalance(UUID caisseId) {
        return financeService.getCaisseBalance(caisseId);
    }

    @Transactional(readOnly = true)
    public JournalCaisseResponse getJournalCaisse(UUID caisseId, LocalDate debut, LocalDate fin) {
        return financeService.getJournalCaisse(caisseId, debut, fin);
    }

    public TransfertResponse createTransfert(TransfertRequest request) {
        if (request.getCaisseSourceId().equals(request.getCaisseDestinationId())) {
            throw new IllegalArgumentException("La caisse source et la caisse destination doivent être différentes");
        }

        Caisse caisseSource = findCaisseOrThrow(request.getCaisseSourceId());
        Caisse caisseDestination = findCaisseOrThrow(request.getCaisseDestinationId());

        BigDecimal soldeSource = financeService.calculateBalance(caisseSource.getId());
        if (soldeSource.compareTo(request.getMontant()) < 0) {
            throw new IllegalStateException("Solde insuffisant dans la caisse source " + caisseSource.getNumero()
                    + ": solde=" + soldeSource + ", montant=" + request.getMontant());
        }

        Transfert transfert = transfertMapper.toEntity(request);
        transfert.setId(UUID.randomUUID());
        transfert.setStatut("EFFECTUE");
        Transfert saved = transfertRepository.save(transfert);

        List<AccountingEntryRequest> entries = new ArrayList<>();
        entries.add(AccountingEntryRequest.builder()
                .caisseId(request.getCaisseSourceId())
                .montant(request.getMontant())
                .sens(Sens.SORTIE)
                .type(MouvementType.TRANSFERT_OUT)
                .description("Transfert vers " + caisseDestination.getNumero()
                        + (request.getMotif() != null ? " - " + request.getMotif() : ""))
                .referenceType("TRANSFERT")
                .referenceId(saved.getId())
                .build());
        entries.add(AccountingEntryRequest.builder()
                .caisseId(request.getCaisseDestinationId())
                .montant(request.getMontant())
                .sens(Sens.ENTREE)
                .type(MouvementType.TRANSFERT_IN)
                .description("Transfert depuis " + caisseSource.getNumero()
                        + (request.getMotif() != null ? " - " + request.getMotif() : ""))
                .referenceType("TRANSFERT")
                .referenceId(saved.getId())
                .build());

        financeService.recordDoubleEntry(entries);

        log.info("Created transfert: {} from {} to {}, amount={}",
                saved.getId(), caisseSource.getNumero(), caisseDestination.getNumero(), request.getMontant());

        return transfertMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<TransfertResponse> listTransferts() {
        return transfertMapper.toResponseList(transfertRepository.findAll());
    }

    public ApprovisionnementResponse createApprovisionnement(ApprovisionnementRequest request) {
        Caisse caisse = findCaisseOrThrow(request.getCaisseId());

        Approvisionnement approvisionnement = approvisionnementMapper.toEntity(request);
        approvisionnement.setId(UUID.randomUUID());
        Approvisionnement saved = approvisionnementRepository.save(approvisionnement);

        List<AccountingEntryRequest> entries = new ArrayList<>();

        AccountingEntryRequest entry = AccountingEntryRequest.builder()
                .caisseId(request.getCaisseId())
                .montant(request.getMontant())
                .sens(Sens.ENTREE)
                .type(MouvementType.APPROVISIONNEMENT)
                .description("Approvisionnement " + request.getModeApprovisionnement()
                        + (request.getMotif() != null ? " - " + request.getMotif() : ""))
                .referenceType("APPROVISIONNEMENT")
                .referenceId(saved.getId())
                .build();
        entries.add(entry);

        financeService.recordDoubleEntry(entries);

        log.info("Created approvisionnement: {} for caisse {}, amount={}",
                saved.getId(), caisse.getNumero(), request.getMontant());

        return approvisionnementMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ApprovisionnementResponse> listApprovisionnements() {
        return approvisionnementMapper.toResponseList(approvisionnementRepository.findAll());
    }

    public SortieCaisseResponse createSortie(SortieCaisseRequest request) {
        Caisse caisse = findCaisseOrThrow(request.getCaisseId());

        BigDecimal soldeActuel = financeService.calculateBalance(caisse.getId());
        if (soldeActuel.compareTo(request.getMontant()) < 0) {
            throw new IllegalStateException("Solde insuffisant dans la caisse " + caisse.getNumero()
                    + ": solde=" + soldeActuel + ", montant=" + request.getMontant());
        }

        SortieCaisse sortieCaisse = sortieCaisseMapper.toEntity(request);
        sortieCaisse.setId(UUID.randomUUID());
        SortieCaisse saved = sortieCaisseRepository.save(sortieCaisse);

        List<AccountingEntryRequest> entries = new ArrayList<>();

        AccountingEntryRequest entry = AccountingEntryRequest.builder()
                .caisseId(request.getCaisseId())
                .montant(request.getMontant())
                .sens(Sens.SORTIE)
                .type(MouvementType.PAIEMENT)
                .description("Sortie caisse " + request.getTypeSortie()
                        + " - " + request.getMotif())
                .referenceType("SORTIE_CAISSE")
                .referenceId(saved.getId())
                .build();
        entries.add(entry);

        financeService.recordDoubleEntry(entries);

        log.info("Created sortie caisse: {} from caisse {}, amount={}",
                saved.getId(), caisse.getNumero(), request.getMontant());

        return sortieCaisseMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SortieCaisseResponse> listSorties() {
        return sortieCaisseMapper.toResponseList(sortieCaisseRepository.findAll());
    }

    private Caisse findCaisseOrThrow(UUID id) {
        return caisseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caisse", id.toString()));
    }

    private String getCaissePrefix(CaisseType type) {
        return switch (type) {
            case SYSTEME -> "SYS";
            case PHYSIQUE -> "PHY";
            case MEMBRE -> "MBR";
            case COLLECTEUR -> "COL";
            case COURANT -> "CAG-COU";
            case EPARGNE -> "CAG-EPG";
            case TONTINE -> "TON";
            case CREDIT -> "CRE";
            case IMPAYES -> "IMP";
        };
    }
}
