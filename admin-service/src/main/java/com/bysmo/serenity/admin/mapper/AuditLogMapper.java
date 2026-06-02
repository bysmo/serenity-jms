package com.bysmo.serenity.admin.mapper;

import com.bysmo.serenity.admin.dto.AuditLogResponse;
import com.bysmo.serenity.admin.entity.AuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(target = "actorType", source = "actorType", qualifiedByName = "actorTypeToString")
    AuditLogResponse toResponse(AuditLog entity);

    List<AuditLogResponse> toResponseList(List<AuditLog> entities);

    @org.mapstruct.Named("actorTypeToString")
    default String actorTypeToString(com.bysmo.serenity.admin.entity.enums.ActorType actorType) {
        return actorType != null ? actorType.name() : null;
    }
}
