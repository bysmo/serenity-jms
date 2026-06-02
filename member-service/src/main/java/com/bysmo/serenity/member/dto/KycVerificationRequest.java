package com.bysmo.serenity.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KycVerificationRequest {

    private UUID membreId;
    private String niveau;
    private String commentaire;
}
