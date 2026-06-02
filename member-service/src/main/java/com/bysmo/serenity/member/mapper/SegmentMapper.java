package com.bysmo.serenity.member.mapper;

import com.bysmo.serenity.member.dto.SegmentRequest;
import com.bysmo.serenity.member.dto.SegmentResponse;
import com.bysmo.serenity.member.entity.Segment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SegmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Segment toEntity(SegmentRequest request);

    SegmentResponse toResponse(Segment segment);

    List<SegmentResponse> toResponseList(List<Segment> segments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(SegmentRequest request, @MappingTarget Segment segment);
}
