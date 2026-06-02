package com.serenity.nanocredit.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRequest {

    @NotNull(message = "L'identifiant du membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "L'identifiant du palier est obligatoire")
    private UUID palierId;
}
