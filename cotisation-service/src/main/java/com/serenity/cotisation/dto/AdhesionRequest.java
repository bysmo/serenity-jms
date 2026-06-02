package com.serenity.cotisation.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdhesionRequest {

    @NotNull(message = "Le membre est obligatoire")
    private UUID membreId;

    @NotNull(message = "La cotisation est obligatoire")
    private UUID cotisationId;
}
