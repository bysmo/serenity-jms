package com.serenity.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MemberCreatedEvent extends BaseEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID memberId;
    private String numero;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private UUID segmentId;
    private UUID parrainId;
}
