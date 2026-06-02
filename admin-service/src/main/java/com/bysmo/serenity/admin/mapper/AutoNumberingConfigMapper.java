package com.bysmo.serenity.admin.mapper;

import com.bysmo.serenity.admin.dto.AutoNumberingConfigRequest;
import com.bysmo.serenity.admin.dto.AutoNumberingConfigResponse;
import com.bysmo.serenity.admin.entity.AutoNumberingConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutoNumberingConfigMapper {

    AutoNumberingConfigResponse toResponse(AutoNumberingConfig entity);

    List<AutoNumberingConfigResponse> toResponseList(List<AutoNumberingConfig> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentValue", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AutoNumberingConfig toEntity(AutoNumberingConfigRequest request);
}
