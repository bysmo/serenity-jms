package com.serenity.admin.mapper;

import com.serenity.admin.dto.EmailTemplateRequest;
import com.serenity.admin.dto.EmailTemplateResponse;
import com.serenity.admin.entity.EmailTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmailTemplateMapper {

    EmailTemplateResponse toResponse(EmailTemplate entity);

    List<EmailTemplateResponse> toResponseList(List<EmailTemplate> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    EmailTemplate toEntity(EmailTemplateRequest request);
}
