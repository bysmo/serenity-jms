package com.serenity.admin.service;

import com.serenity.admin.dto.TagRequest;
import com.serenity.admin.dto.TagResponse;
import com.serenity.admin.entity.Tag;
import com.serenity.admin.mapper.TagMapper;
import com.serenity.admin.repository.TagRepository;
import com.serenity.common.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagResponse> getAll() {
        log.debug("Fetching all tags");
        List<Tag> tags = tagRepository.findAll();
        return tagMapper.toResponseList(tags);
    }

    public List<TagResponse> getByType(String type) {
        log.debug("Fetching tags by type: {}", type);
        List<Tag> tags = tagRepository.findByType(type);
        return tagMapper.toResponseList(tags);
    }

    @Transactional
    public TagResponse create(TagRequest request) {
        log.info("Creating tag: {}", request.getNom());

        Tag entity = tagMapper.toEntity(request);
        Tag saved = tagRepository.save(entity);

        log.info("Tag created with id: {}", saved.getId());
        return tagMapper.toResponse(saved);
    }

    @Transactional
    public TagResponse update(UUID id, TagRequest request) {
        log.info("Updating tag with id: {}", id);

        Tag existing = tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag", id));

        existing.setNom(request.getNom());
        existing.setType(request.getType());
        existing.setDescription(request.getDescription());

        Tag saved = tagRepository.save(existing);
        log.info("Tag updated with id: {}", saved.getId());
        return tagMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deleting tag with id: {}", id);
        if (!tagRepository.existsById(id)) {
            throw new EntityNotFoundException("Tag", id);
        }
        tagRepository.deleteById(id);
        log.info("Tag deleted with id: {}", id);
    }
}
