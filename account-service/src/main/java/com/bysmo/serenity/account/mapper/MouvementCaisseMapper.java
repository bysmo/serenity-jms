package com.bysmo.serenity.account.mapper;

import com.bysmo.serenity.account.dto.MouvementCaisseRequest;
import com.bysmo.serenity.account.dto.MouvementCaisseResponse;
import com.bysmo.serenity.account.entity.MouvementCaisse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MouvementCaisseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "soldeAvant", ignore = true)
    @Mapping(target = "soldeApres", ignore = true)
    @Mapping(target = "dateOperation", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MouvementCaisse toEntity(MouvementCaisseRequest request);

    MouvementCaisseResponse toResponse(MouvementCaisse mouvement);

    List<MouvementCaisseResponse> toResponseList(List<MouvementCaisse> mouvements);
}
