package com.serenity.admin.dto;

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
public class AutoNumberingConfigResponse {

    private UUID id;
    private String objectType;
    private String definition;
    private Long currentValue;
    private Boolean isActive;
    private String checksum;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
