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
public class WalletAliasRequest {

    @NotBlank(message = "L'alias est obligatoire")
    @Size(max = 100)
    private String alias;

    @Builder.Default
    private String type = "USERNAME";

    @Builder.Default
    private Boolean isPrimary = false;
}
