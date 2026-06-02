package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.KycStatut;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycVerificationResponse {

    private UUID id;
    private UUID membreId;
    private KycStatut statut;
    private String niveau;
    private UUID validatedBy;
    private LocalDateTime validatedAt;
    private UUID rejectedBy;
    private LocalDateTime rejectedAt;
    private String motifRejet;
    private String commentaire;
    private List<KycDocumentResponse> documents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
