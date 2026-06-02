package com.bysmo.serenity.cotisation.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOverdueEvent {

    private UUID engagementId;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montantEngage;
    private BigDecimal montantPaye;
    private LocalDate periodeDebut;
    private LocalDate periodeFin;
    private String statut;
    private LocalDateTime eventTimestamp;

    public static PaymentOverdueEvent from(UUID engagementId, UUID cotisationId, UUID membreId,
                                            BigDecimal montantEngage, BigDecimal montantPaye,
                                            LocalDate periodeDebut, LocalDate periodeFin, String statut) {
        return PaymentOverdueEvent.builder()
                .engagementId(engagementId)
                .cotisationId(cotisationId)
                .membreId(membreId)
                .montantEngage(montantEngage)
                .montantPaye(montantPaye)
                .periodeDebut(periodeDebut)
                .periodeFin(periodeFin)
                .statut(statut)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}
