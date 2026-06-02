package com.bysmo.serenity.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
    private List<FieldError> details;

    public static ErrorResponse of(String message) {
        return ErrorResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static ErrorResponse of(String message, List<FieldError> details) {
        return ErrorResponse.builder()
                .message(message)
                .timestamp(LocalDateTime.now())
                .details(details)
                .build();
    }
}
