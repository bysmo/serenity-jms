package com.serenity.admin.mapper;

import com.serenity.admin.dto.AppSettingRequest;
import com.serenity.admin.dto.AppSettingResponse;
import com.serenity.admin.entity.AppSetting;
import com.serenity.admin.entity.enums.SettingType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AppSettingMapper {

    @Mapping(target = "type", source = "type", qualifiedByName = "settingTypeToString")
    AppSettingResponse toResponse(AppSetting entity);

    List<AppSettingResponse> toResponseList(List<AppSetting> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "type", source = "type", qualifiedByName = "stringToSettingType")
    AppSetting toEntity(AppSettingRequest request);

    @Named("settingTypeToString")
    default String settingTypeToString(SettingType type) {
        return type != null ? type.name() : null;
    }

    @Named("stringToSettingType")
    default SettingType stringToSettingType(String type) {
        return type != null ? SettingType.valueOf(type.toUpperCase()) : SettingType.STRING;
    }
}
