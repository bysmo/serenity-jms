package com.bysmo.serenity.member.service;

import com.bysmo.serenity.member.dto.SegmentRequest;
import com.bysmo.serenity.member.dto.SegmentResponse;
import com.bysmo.serenity.member.entity.Segment;
import com.bysmo.serenity.member.exception.BusinessException;
import com.bysmo.serenity.member.exception.DuplicateResourceException;
import com.bysmo.serenity.member.exception.ResourceNotFoundException;
import com.bysmo.serenity.member.mapper.SegmentMapper;
import com.bysmo.serenity.member.repository.SegmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SegmentService {

    private final SegmentRepository segmentRepository;
    private final SegmentMapper segmentMapper;

    @Transactional(readOnly = true)
    public List<SegmentResponse> getAll() {
        log.debug("Fetching all segments");
        return segmentMapper.toResponseList(segmentRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<SegmentResponse> getActiveSegments() {
        log.debug("Fetching active segments");
        return segmentMapper.toResponseList(segmentRepository.findByActifTrue());
    }

    @Transactional(readOnly = true)
    public SegmentResponse getById(UUID id) {
        log.debug("Fetching segment by id: {}", id);
        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment", "id", id));
        return segmentMapper.toResponse(segment);
    }

    @Transactional(readOnly = true)
    public SegmentResponse getBySlug(String slug) {
        log.debug("Fetching segment by slug: {}", slug);
        Segment segment = segmentRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Segment", "slug", slug));
        return segmentMapper.toResponse(segment);
    }

    @Transactional
    public SegmentResponse create(SegmentRequest request) {
        log.info("Creating segment: {}", request.getNom());

        if (segmentRepository.findBySlug(request.getSlug()).isPresent()) {
            throw new DuplicateResourceException("Segment", "slug", request.getSlug());
        }

        Segment segment = segmentMapper.toEntity(request);
        segment = segmentRepository.save(segment);

        log.info("Segment created with id: {}", segment.getId());
        return segmentMapper.toResponse(segment);
    }

    @Transactional
    public SegmentResponse update(UUID id, SegmentRequest request) {
        log.info("Updating segment: {}", id);

        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment", "id", id));

        if (!segment.getSlug().equals(request.getSlug())) {
            segmentRepository.findBySlug(request.getSlug()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("Segment", "slug", request.getSlug());
                }
            });
        }

        segmentMapper.updateEntityFromRequest(request, segment);
        segment = segmentRepository.save(segment);

        log.info("Segment updated: {}", id);
        return segmentMapper.toResponse(segment);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting segment: {}", id);

        Segment segment = segmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Segment", "id", id));

        if (segment.getIsDefault()) {
            throw new BusinessException("Impossible de supprimer le segment par défaut");
        }

        segmentRepository.delete(segment);
        log.info("Segment deleted: {}", id);
    }

    @Transactional(readOnly = true)
    public Segment getDefaultSegment() {
        return segmentRepository.findByIsDefaultTrue()
                .orElseThrow(() -> new BusinessException("Aucun segment par défaut configuré"));
    }
}
