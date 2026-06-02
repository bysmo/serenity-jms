package com.serenity.member.mapper;

import com.serenity.member.dto.KycDocumentRequest;
import com.serenity.member.dto.KycDocumentResponse;
import com.serenity.member.entity.KycDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface KycDocumentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "kycVerificationId", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    KycDocument toEntity(KycDocumentRequest request);

    KycDocumentResponse toResponse(KycDocument document);

    List<KycDocumentResponse> toResponseList(List<KycDocument> documents);
}
