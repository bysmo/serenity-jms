package com.bysmo.serenity.account.mapper;

import com.bysmo.serenity.account.dto.TransfertRequest;
import com.bysmo.serenity.account.dto.TransfertResponse;
import com.bysmo.serenity.account.entity.Transfert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransfertMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "statut", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Transfert toEntity(TransfertRequest request);

    TransfertResponse toResponse(Transfert transfert);

    List<TransfertResponse> toResponseList(List<Transfert> transferts);
}
