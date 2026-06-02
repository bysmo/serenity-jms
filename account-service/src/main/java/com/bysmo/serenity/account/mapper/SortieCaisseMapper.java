package com.bysmo.serenity.account.mapper;

import com.bysmo.serenity.account.dto.SortieCaisseRequest;
import com.bysmo.serenity.account.dto.SortieCaisseResponse;
import com.bysmo.serenity.account.entity.SortieCaisse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SortieCaisseMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SortieCaisse toEntity(SortieCaisseRequest request);

    SortieCaisseResponse toResponse(SortieCaisse sortieCaisse);

    List<SortieCaisseResponse> toResponseList(List<SortieCaisse> sorties);
}
