package com.serenity.common.exception;

public class UnbalancedEntryException extends RuntimeException {

    public UnbalancedEntryException(String message) {
        super(message);
    }

    public UnbalancedEntryException(String message, Throwable cause) {
        super(message, cause);
    }
}
