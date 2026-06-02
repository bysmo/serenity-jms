package com.bysmo.serenity.member.service;

import com.bysmo.serenity.member.dto.CompteExterneRequest;
import com.bysmo.serenity.member.dto.CompteExterneResponse;
import com.bysmo.serenity.member.entity.MembreCompteExterne;
import com.bysmo.serenity.member.exception.DuplicateResourceException;
import com.bysmo.serenity.member.exception.ResourceNotFoundException;
import com.bysmo.serenity.member.mapper.CompteExterneMapper;
import com.bysmo.serenity.member.repository.MembreCompteExterneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompteExterneService {

    private final MembreCompteExterneRepository compteExterneRepository;
    private final CompteExterneMapper compteExterneMapper;

    @Transactional(readOnly = true)
    public List<CompteExterneResponse> getByMembreId(UUID membreId) {
        log.debug("Fetching external accounts for member: {}", membreId);
        List<MembreCompteExterne> comptes = compteExterneRepository.findByMembreId(membreId);
        return compteExterneMapper.toResponseList(comptes);
    }

    @Transactional(readOnly = true)
    public CompteExterneResponse getById(UUID id) {
        log.debug("Fetching external account by id: {}", id);
        MembreCompteExterne compte = compteExterneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreCompteExterne", "id", id));
        return compteExterneMapper.toResponse(compte);
    }

    @Transactional
    public CompteExterneResponse create(UUID membreId, CompteExterneRequest request) {
        log.info("Creating external account for member: {}", membreId);

        compteExterneRepository.findByIdentifiant(request.getIdentifiant()).ifPresent(c -> {
            throw new DuplicateResourceException("MembreCompteExterne", "identifiant", request.getIdentifiant());
        });

        MembreCompteExterne compte = compteExterneMapper.toEntity(request);
        compte.setMembreId(membreId);
        compte.setActif(true);

        compte = compteExterneRepository.save(compte);

        log.info("External account created with id: {} for member: {}", compte.getId(), membreId);
        return compteExterneMapper.toResponse(compte);
    }

    @Transactional
    public CompteExterneResponse update(UUID id, CompteExterneRequest request) {
        log.info("Updating external account: {}", id);

        MembreCompteExterne compte = compteExterneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreCompteExterne", "id", id));

        if (!compte.getIdentifiant().equals(request.getIdentifiant())) {
            compteExterneRepository.findByIdentifiant(request.getIdentifiant()).ifPresent(c -> {
                if (!c.getId().equals(id)) {
                    throw new DuplicateResourceException("MembreCompteExterne", "identifiant", request.getIdentifiant());
                }
            });
        }

        compteExterneMapper.updateEntityFromRequest(request, compte);
        compte = compteExterneRepository.save(compte);

        log.info("External account updated: {}", id);
        return compteExterneMapper.toResponse(compte);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting external account: {}", id);
        MembreCompteExterne compte = compteExterneRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MembreCompteExterne", "id", id));
        compteExterneRepository.delete(compte);
        log.info("External account deleted: {}", id);
    }
}
