package com.bysmo.serenity.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinVerifyRequest {

    @NotBlank(message = "Le code PIN est obligatoire")
    private String codePin;
}
