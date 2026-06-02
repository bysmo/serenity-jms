package com.bysmo.serenity.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class AppSettingRequest {

    @NotBlank(message = "Key is required")
    private String cle;

    private String valeur;

    @NotNull(message = "Type is required")
    private String type;

    private String groupe;
}
