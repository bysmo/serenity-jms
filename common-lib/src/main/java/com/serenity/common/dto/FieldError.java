package com.serenity.common.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldError {

    private final String field;
    private final String message;
    private final Object rejectedValue;

    public static FieldError of(String field, String message, Object rejectedValue) {
        return new FieldError(field, message, rejectedValue);
    }
}
