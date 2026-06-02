package com.bysmo.serenity.common.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseEvent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private UUID eventId = UUID.randomUUID();

    private String eventType;

    private LocalDateTime timestamp = LocalDateTime.now();

    private String sourceService;

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "eventId=" + eventId +
                ", eventType='" + eventType + '\'' +
                ", timestamp=" + timestamp +
                ", sourceService='" + sourceService + '\'' +
                '}';
    }
}
