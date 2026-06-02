package com.serenity.member.mapper;

import com.serenity.member.dto.ParrainageCommissionResponse;
import com.serenity.member.entity.ParrainageCommission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParrainageCommissionMapper {

    ParrainageCommissionResponse toResponse(ParrainageCommission commission);

    List<ParrainageCommissionResponse> toResponseList(List<ParrainageCommission> commissions);
}
