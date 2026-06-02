package com.bysmo.serenity.collector.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfirmCollectRequest {

    @NotBlank(message = "Le code OTP est obligatoire")
    private String otpCode;
}
