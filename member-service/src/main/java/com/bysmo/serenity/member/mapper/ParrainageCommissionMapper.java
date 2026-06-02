package com.bysmo.serenity.member.mapper;

import com.bysmo.serenity.member.dto.ParrainageCommissionResponse;
import com.bysmo.serenity.member.entity.ParrainageCommission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ParrainageCommissionMapper {

    ParrainageCommissionResponse toResponse(ParrainageCommission commission);

    List<ParrainageCommissionResponse> toResponseList(List<ParrainageCommission> commissions);
}
