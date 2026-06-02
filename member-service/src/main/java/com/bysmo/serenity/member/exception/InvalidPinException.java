package com.bysmo.serenity.member.exception;

public class InvalidPinException extends RuntimeException {

    private final int attemptsRemaining;

    public InvalidPinException(int attemptsRemaining) {
        super(String.format("Code PIN incorrect. %d tentative(s) restante(s).", attemptsRemaining));
        this.attemptsRemaining = attemptsRemaining;
    }

    public int getAttemptsRemaining() {
        return attemptsRemaining;
    }
}
