package com.serenity.nanocredit.client.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisbursementRequest {

    private String telephone;
    private BigDecimal montant;
    private String withdrawMode;
    private String gateway;
    private String internalReference;
}
