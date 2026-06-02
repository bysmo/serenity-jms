package com.bysmo.serenity.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinSetupRequest {

    @NotBlank(message = "Le code PIN est obligatoire")
    @Size(min = 4, max = 6, message = "Le code PIN doit contenir entre 4 et 6 chiffres")
    private String codePin;
}
