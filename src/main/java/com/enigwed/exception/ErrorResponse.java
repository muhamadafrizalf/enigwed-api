package com.enigwed.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String error;

    public ErrorResponse(HttpStatus httpStatus, String message, String error) {
        super(message);
        this.httpStatus = httpStatus;
        this.error = error;
    }
}
