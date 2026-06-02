package com.serenity.admin.mapper;

import com.serenity.admin.dto.AnnonceRequest;
import com.serenity.admin.dto.AnnonceResponse;
import com.serenity.admin.entity.Annonce;
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
