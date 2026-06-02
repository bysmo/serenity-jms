package com.bysmo.serenity.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PaymentCompletedEvent extends BaseEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID paiementId;
    private UUID membreId;
    private UUID cotisationId;
    private BigDecimal montant;
    private String modePaiement;
}
