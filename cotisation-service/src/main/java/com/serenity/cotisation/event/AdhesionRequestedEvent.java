package com.serenity.cotisation.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdhesionRequestedEvent {

    private UUID adhesionId;
    private UUID cotisationId;
    private UUID membreId;
    private String statut;
    private LocalDateTime eventTimestamp;

    public static AdhesionRequestedEvent from(UUID adhesionId, UUID cotisationId, UUID membreId, String statut) {
        return AdhesionRequestedEvent.builder()
                .adhesionId(adhesionId)
                .cotisationId(cotisationId)
                .membreId(membreId)
                .statut(statut)
                .eventTimestamp(LocalDateTime.now())
                .build();
    }
}
