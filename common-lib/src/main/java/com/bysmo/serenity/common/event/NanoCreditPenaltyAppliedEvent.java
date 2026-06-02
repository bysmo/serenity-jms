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
public class NanoCreditPenaltyAppliedEvent extends BaseEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID nanoCreditId;
    private UUID membreId;
    private BigDecimal montantPenalite;
    private int joursRetard;
}
