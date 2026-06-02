package com.serenity.admin.service;

import com.serenity.admin.dto.AppSettingRequest;
import com.serenity.admin.dto.AppSettingResponse;
import com.serenity.admin.entity.AppSetting;
import com.serenity.admin.entity.enums.SettingType;
import com.serenity.admin.mapper.AppSettingMapper;
import com.serenity.admin.repository.AppSettingRepository;
import com.serenity.common.exception.DuplicateResourceException;
import com.serenity.common.exception.EntityNotFoundException;
import com.serenity.common.util.ChecksumUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppSettingService {

    private final AppSettingRepository appSettingRepository;
    private final AppSettingMapper appSettingMapper;

    @Cacheable(value = "app-settings", key = "'all'")
    public Page<AppSettingResponse> getAll(Pageable pageable) {
        log.debug("Fetching all app settings - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        Page<AppSetting> settings = appSettingRepository.findAll(pageable);
        return settings.map(appSettingMapper::toResponse);
    }

    @Cacheable(value = "app-settings", key = "#cle")
    public AppSettingResponse getByCle(String cle) {
        log.debug("Fetching app setting by key: {}", cle);
        AppSetting setting = appSettingRepository.findByCle(cle)
                .orElseThrow(() -> new EntityNotFoundException("AppSetting", cle));
        return appSettingMapper.toResponse(setting);
    }

    @Cacheable(value = "app-settings", key = "'group:' + #groupe")
    public List<AppSettingResponse> getByGroupe(String groupe) {
        log.debug("Fetching app settings by group: {}", groupe);
        List<AppSetting> settings = appSettingRepository.findByGroupe(groupe);
        return appSettingMapper.toResponseList(settings);
    }

    @Transactional
    @CacheEvict(value = "app-settings", allEntries = true)
    public AppSettingResponse create(AppSettingRequest request) {
        log.info("Creating app setting with key: {}", request.getCle());

        if (appSettingRepository.findByCle(request.getCle()).isPresent()) {
            throw new DuplicateResourceException("App setting with key '" + request.getCle() + "' already exists");
        }

        AppSetting entity = appSettingMapper.toEntity(request);
        entity.setChecksum(computeChecksum(entity));
        AppSetting saved = appSettingRepository.save(entity);

        log.info("App setting created with id: {}", saved.getId());
        return appSettingMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "app-settings", allEntries = true)
    public AppSettingResponse update(UUID id, AppSettingRequest request) {
        log.info("Updating app setting with id: {}", id);

        AppSetting existing = appSettingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("AppSetting", id));

        if (!existing.getCle().equals(request.getCle())) {
            if (appSettingRepository.findByCle(request.getCle()).isPresent()) {
                throw new DuplicateResourceException("App setting with key '" + request.getCle() + "' already exists");
            }
        }

        existing.setCle(request.getCle());
        existing.setValeur(request.getValeur());
        existing.setType(request.getType() != null ? SettingType.valueOf(request.getType().toUpperCase()) : SettingType.STRING);
        existing.setGroupe(request.getGroupe());
        existing.setChecksum(computeChecksum(existing));

        AppSetting saved = appSettingRepository.save(existing);
        log.info("App setting updated with id: {}", saved.getId());
        return appSettingMapper.toResponse(saved);
    }

    @Transactional
    @CacheEvict(value = "app-settings", allEntries = true)
    public void delete(UUID id) {
        log.info("Deleting app setting with id: {}", id);
        if (!appSettingRepository.existsById(id)) {
            throw new EntityNotFoundException("AppSetting", id);
        }
        appSettingRepository.deleteById(id);
        log.info("App setting deleted with id: {}", id);
    }

    private String computeChecksum(AppSetting setting) {
        String data = setting.getCle() + "|" + setting.getValeur() + "|" + setting.getType() + "|" + setting.getGroupe();
        return ChecksumUtil.sha256(data);
    }
}
