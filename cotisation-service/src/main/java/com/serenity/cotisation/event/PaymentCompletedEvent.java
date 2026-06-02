package com.serenity.cotisation.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCompletedEvent {

    private UUID paiementId;
    private UUID cotisationId;
    private UUID membreId;
    private BigDecimal montant;
    private String modePaiement;
    private String statut;
    private LocalDateTime datePaiement;
    private LocalDateTime eventTimestamp;

    public static PaymentCompletedEvent from(UUID paiementId, UUID cotisationId, UUID membreId,
                                              BigDecimal montant, String modePaiement, String statut,
                                              LocalDateTime datePaiement) {
        return PaymentCompletedEvent.builder()
                .paiementId(paiementId)
                .cotisationId(cotisationId)
                .membreId(membreId)
                .montant(montant)
                .modePaiement(modePaiement)
                .statut(statut)
                .datePaiement(datePaiement)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}
