package com.bysmo.serenity.admin.mapper;

import com.bysmo.serenity.admin.dto.AnnonceRequest;
import com.bysmo.serenity.admin.dto.AnnonceResponse;
import com.bysmo.serenity.admin.entity.Annonce;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnnonceMapper {

    AnnonceResponse toResponse(Annonce entity);

    List<AnnonceResponse> toResponseList(List<Annonce> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Annonce toEntity(AnnonceRequest request);
}
