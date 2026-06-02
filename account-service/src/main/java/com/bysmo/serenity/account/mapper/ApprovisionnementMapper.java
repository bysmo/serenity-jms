package com.bysmo.serenity.account.mapper;

import com.bysmo.serenity.account.dto.ApprovisionnementRequest;
import com.bysmo.serenity.account.dto.ApprovisionnementResponse;
import com.bysmo.serenity.account.entity.Approvisionnement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApprovisionnementMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Approvisionnement toEntity(ApprovisionnementRequest request);

    ApprovisionnementResponse toResponse(Approvisionnement approvisionnement);

    List<ApprovisionnementResponse> toResponseList(List<Approvisionnement> approvisionnements);
}
