package com.enigwed.controller;

import com.enigwed.constant.SMessage;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.JwtAuthenticationException;
import com.enigwed.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ErrorController {

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        ApiResponse<?> response = ApiResponse.failed(e.getMessage(), e.getError());
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<?> handle(JwtAuthenticationException e) {
        ApiResponse<?> response = ApiResponse.failed(SMessage.AUTHENTICATION_FAILED, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handle(AuthenticationException e) {
        ApiResponse<?> response = ApiResponse.failed(SMessage.UNAUTHORIZED, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handle(ValidationException e) {
        ApiResponse<?> response = ApiResponse.failed(e.getMessage(), e.getErrors().get(0));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        log.error("UNHANDLED ERROR: {}",e.getMessage(), e);
        ApiResponse<?> response = ApiResponse.failed(SMessage.ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
