package com.bysmo.serenity.admin.mapper;

import com.bysmo.serenity.admin.dto.TagRequest;
import com.bysmo.serenity.admin.dto.TagResponse;
import com.bysmo.serenity.admin.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TagMapper {

    TagResponse toResponse(Tag entity);

    List<TagResponse> toResponseList(List<Tag> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Tag toEntity(TagRequest request);
}
