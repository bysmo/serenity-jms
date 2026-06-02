package com.bysmo.serenity.account.mapper;

import com.bysmo.serenity.account.dto.CaisseRequest;
import com.bysmo.serenity.account.dto.CaisseResponse;
import com.bysmo.serenity.account.entity.Caisse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CaisseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Caisse toEntity(CaisseRequest request);

    CaisseResponse toResponse(Caisse caisse);

    List<CaisseResponse> toResponseList(List<Caisse> caisses);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "numero", ignore = true)
    @Mapping(target = "checksum", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(CaisseRequest request, @MappingTarget Caisse caisse);
}
