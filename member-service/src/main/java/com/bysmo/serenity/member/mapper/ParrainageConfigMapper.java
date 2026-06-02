package com.bysmo.serenity.member.mapper;

import com.bysmo.serenity.member.dto.ParrainageConfigRequest;
import com.bysmo.serenity.member.dto.ParrainageConfigResponse;
import com.bysmo.serenity.member.entity.ParrainageConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ParrainageConfigMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ParrainageConfig toEntity(ParrainageConfigRequest request);

    ParrainageConfigResponse toResponse(ParrainageConfig config);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ParrainageConfigRequest request, @MappingTarget ParrainageConfig config);
}
