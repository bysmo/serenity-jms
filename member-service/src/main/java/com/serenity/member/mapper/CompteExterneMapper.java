package com.serenity.member.mapper;

import com.serenity.member.dto.CompteExterneRequest;
import com.serenity.member.dto.CompteExterneResponse;
import com.serenity.member.entity.MembreCompteExterne;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompteExterneMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membreId", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MembreCompteExterne toEntity(CompteExterneRequest request);

    CompteExterneResponse toResponse(MembreCompteExterne compte);

    List<CompteExterneResponse> toResponseList(List<MembreCompteExterne> comptes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "membreId", ignore = true)
    @Mapping(target = "actif", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CompteExterneRequest request, @MappingTarget MembreCompteExterne compte);
}
