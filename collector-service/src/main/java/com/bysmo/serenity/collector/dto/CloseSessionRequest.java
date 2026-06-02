package com.bysmo.serenity.collector.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseSessionRequest {

    private BigDecimal montantFermeture;
}
