package com.serenity.member.exception;

public class PinLockedException extends RuntimeException {

    private final long remainingMinutes;

    public PinLockedException(long remainingMinutes) {
        super(String.format("PIN verrouillé. Veuillez réessayer dans %d minute(s).", remainingMinutes));
        this.remainingMinutes = remainingMinutes;
    }

    public long getRemainingMinutes() {
        return remainingMinutes;
    }
}
