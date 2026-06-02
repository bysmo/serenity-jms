package com.bysmo.serenity.member.service;

import com.bysmo.serenity.member.dto.WalletAliasRequest;
import com.bysmo.serenity.member.dto.WalletAliasResponse;
import com.bysmo.serenity.member.entity.MembreWalletAlias;
import com.bysmo.serenity.member.exception.DuplicateResourceException;
import com.bysmo.serenity.member.exception.ResourceNotFoundException;
import com.bysmo.serenity.member.mapper.WalletAliasMapper;
import com.bysmo.serenity.member.repository.MembreWalletAliasRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletAliasService {

    private final MembreWalletAliasRepository walletAliasRepository;
    private final WalletAliasMapper walletAliasMapper;

    @Transactional(readOnly = true)
    public List<WalletAliasResponse> getByMembreId(UUID membreId) {
        log.debug("Fetching wallet aliases for member: {}", membreId);
        List<MembreWalletAlias> aliases = walletAliasRepository.findByMembreId(membreId);
        return walletAliasMapper.toResponseList(aliases);
    }

    @Transactional(readOnly = true)
    public WalletAliasResponse getById(UUID id) {
        log.debug("Fetching wallet alias by id: {}", id);
        MembreWalletAlias alias = walletAliasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreWalletAlias", "id", id));
        return walletAliasMapper.toResponse(alias);
    }

    @Transactional(readOnly = true)
    public WalletAliasResponse getByAlias(String aliasValue) {
        log.debug("Fetching wallet alias by alias: {}", aliasValue);
        MembreWalletAlias alias = walletAliasRepository.findByAlias(aliasValue)
                .orElseThrow(() -> new ResourceNotFoundException("MembreWalletAlias", "alias", aliasValue));
        return walletAliasMapper.toResponse(alias);
    }

    @Transactional
    public WalletAliasResponse create(UUID membreId, WalletAliasRequest request) {
        log.info("Creating wallet alias for member: {}", membreId);

        walletAliasRepository.findByAlias(request.getAlias()).ifPresent(a -> {
            throw new DuplicateResourceException("MembreWalletAlias", "alias", request.getAlias());
        });

        MembreWalletAlias alias = walletAliasMapper.toEntity(request);
        alias.setMembreId(membreId);
        alias.setActif(true);

        alias = walletAliasRepository.save(alias);

        log.info("Wallet alias created with id: {} for member: {}", alias.getId(), membreId);
        return walletAliasMapper.toResponse(alias);
    }

    @Transactional
    public WalletAliasResponse update(UUID id, WalletAliasRequest request) {
        log.info("Updating wallet alias: {}", id);

        MembreWalletAlias alias = walletAliasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreWalletAlias", "id", id));

        if (!alias.getAlias().equals(request.getAlias())) {
            walletAliasRepository.findByAlias(request.getAlias()).ifPresent(a -> {
                if (!a.getId().equals(id)) {
                    throw new DuplicateResourceException("MembreWalletAlias", "alias", request.getAlias());
                }
            });
        }

        walletAliasMapper.updateEntityFromRequest(request, alias);
        alias = walletAliasRepository.save(alias);

        log.info("Wallet alias updated: {}", id);
        return walletAliasMapper.toResponse(alias);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting wallet alias: {}", id);
        MembreWalletAlias alias = walletAliasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreWalletAlias", "id", id));
        walletAliasRepository.delete(alias);
        log.info("Wallet alias deleted: {}", id);
    }
}
