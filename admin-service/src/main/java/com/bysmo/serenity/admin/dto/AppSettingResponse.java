package com.bysmo.serenity.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppSettingResponse {

    private UUID id;
    private String cle;
    private String valeur;
    private String type;
    private String groupe;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
