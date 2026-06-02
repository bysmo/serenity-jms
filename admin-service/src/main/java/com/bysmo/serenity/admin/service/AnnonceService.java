package com.bysmo.serenity.admin.service;

import com.bysmo.serenity.admin.dto.AnnonceRequest;
import com.bysmo.serenity.admin.dto.AnnonceResponse;
import com.bysmo.serenity.admin.entity.Annonce;
import com.bysmo.serenity.admin.mapper.AnnonceMapper;
import com.bysmo.serenity.admin.repository.AnnonceRepository;
import com.bysmo.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnonceService {

    private final AnnonceRepository annonceRepository;
    private final AnnonceMapper annonceMapper;

    public Page<AnnonceResponse> getAll(Pageable pageable) {
        log.debug("Fetching all annonces - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<Annonce> annonces = annonceRepository.findAll(pageable);
        return annonces.map(annonceMapper::toResponse);
    }

    public List<AnnonceResponse> getActive() {
        log.debug("Fetching active annonces");
        LocalDate today = LocalDate.now();
        // Get annonces that are active and within their date range
        List<Annonce> activeByStatus = new java.util.ArrayList<>(annonceRepository.findByStatut("active"));
        // Also check annonces where today falls between dateDebut and dateFin
        List<Annonce> activeByDate = annonceRepository.findByDateDebutBeforeAndDateFinAfter(today, today);
        // Combine, using a set to avoid duplicates
        Set<UUID> existingIds = activeByStatus.stream()
                .map(Annonce::getId)
                .collect(Collectors.toSet());
        for (Annonce a : activeByDate) {
            if (!existingIds.contains(a.getId())) {
                activeByStatus.add(a);
                existingIds.add(a.getId());
            }
        }
        return annonceMapper.toResponseList(activeByStatus);
    }

    @Transactional
    public AnnonceResponse create(AnnonceRequest request) {
        log.info("Creating annonce: {}", request.getTitre());

        Annonce entity = annonceMapper.toEntity(request);
        if (entity.getStatut() == null) {
            entity.setStatut("active");
        }
        if (entity.getOrdre() == null) {
            entity.setOrdre(0);
        }

        // Auto-expire if date_fin is in the past
        if (entity.getDateFin() != null && entity.getDateFin().isBefore(LocalDate.now())) {
            entity.setStatut("expiree");
        }

        Annonce saved = annonceRepository.save(entity);
        log.info("Annonce created with id: {}", saved.getId());
        return annonceMapper.toResponse(saved);
    }

    @Transactional
    public AnnonceResponse update(UUID id, AnnonceRequest request) {
        log.info("Updating annonce with id: {}", id);

        Annonce existing = annonceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Annonce", id));

        existing.setTitre(request.getTitre());
        existing.setContenu(request.getContenu());
        existing.setDateDebut(request.getDateDebut());
        existing.setDateFin(request.getDateFin());
        existing.setStatut(request.getStatut());
        existing.setType(request.getType());
        existing.setOrdre(request.getOrdre());
        existing.setSegment(request.getSegment());

        // Auto-expire if date_fin is in the past
        if (existing.getDateFin() != null && existing.getDateFin().isBefore(LocalDate.now())) {
            existing.setStatut("expiree");
        }

        Annonce saved = annonceRepository.save(existing);
        log.info("Annonce updated with id: {}", saved.getId());
        return annonceMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting annonce with id: {}", id);
        if (!annonceRepository.existsById(id)) {
            throw new EntityNotFoundException("Annonce", id);
        }
        annonceRepository.deleteById(id);
        log.info("Annonce deleted with id: {}", id);
    }
}
