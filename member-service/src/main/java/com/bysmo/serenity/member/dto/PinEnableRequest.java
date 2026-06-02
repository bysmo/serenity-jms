package com.bysmo.serenity.member.dto;

import com.bysmo.serenity.member.entity.enums.PinMode;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PinEnableRequest {

    @NotNull(message = "Le champ enable est obligatoire")
    private Boolean enable;

    private PinMode mode;
}
