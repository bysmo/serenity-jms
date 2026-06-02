package com.bysmo.serenity.nanocredit.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EtudeRequest {

    @NotNull(message = "Le score AI est obligatoire")
    @PositiveOrZero(message = "Le score AI doit être positif ou nul")
    private BigDecimal scoreAi;

    @NotNull(message = "Le score humain est obligatoire")
    @PositiveOrZero(message = "Le score humain doit être positif ou nul")
    private BigDecimal scoreHumain;
}
