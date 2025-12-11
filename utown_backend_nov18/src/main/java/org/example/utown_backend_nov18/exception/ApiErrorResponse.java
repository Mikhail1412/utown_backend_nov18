package org.example.utown_backend_nov18.exception;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;    // краткий код: BAD_REQUEST, NOT_FOUND и т.п.
    private String message;  // человекочитаемое сообщение
    private String path;     // URI запроса
}
