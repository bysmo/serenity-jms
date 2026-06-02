package com.bysmo.serenity.common.exception;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PinLockedException extends RuntimeException {

    private final LocalDateTime lockedUntil;

    public PinLockedException(String message, LocalDateTime lockedUntil) {
        super(message);
        this.lockedUntil = lockedUntil;
    }

    public PinLockedException(String message, LocalDateTime lockedUntil, Throwable cause) {
        super(message, cause);
        this.lockedUntil = lockedUntil;
    }
}
