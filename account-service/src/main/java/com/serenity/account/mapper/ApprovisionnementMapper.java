package com.serenity.account.mapper;

import com.serenity.account.dto.ApprovisionnementRequest;
import com.serenity.account.dto.ApprovisionnementResponse;
import com.serenity.account.entity.Approvisionnement;
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
