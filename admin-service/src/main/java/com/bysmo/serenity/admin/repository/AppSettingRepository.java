package com.bysmo.serenity.admin.repository;

import com.bysmo.serenity.admin.entity.AppSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppSettingRepository extends JpaRepository<AppSetting, UUID> {

    Optional<AppSetting> findByCle(String cle);

    List<AppSetting> findByGroupe(String groupe);
}
