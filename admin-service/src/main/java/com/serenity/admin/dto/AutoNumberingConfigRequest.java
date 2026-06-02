package com.serenity.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoNumberingConfigRequest {

    @NotBlank(message = "Object type is required")
    private String objectType;

    @NotBlank(message = "Definition is required")
    private String definition;

    private Boolean isActive;
}
