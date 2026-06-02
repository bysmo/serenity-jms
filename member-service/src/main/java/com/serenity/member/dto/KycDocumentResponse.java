package com.serenity.member.dto;

import com.serenity.member.entity.enums.KycDocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycDocumentResponse {

    private UUID id;
    private UUID kycVerificationId;
    private KycDocumentType typeDocument;
    private String nomFichier;
    private String urlFichier;
    private Long tailleFichier;
    private String typeMime;
    private LocalDateTime uploadedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
