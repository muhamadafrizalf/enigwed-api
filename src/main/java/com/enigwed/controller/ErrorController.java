package com.enigwed.controller;

import com.enigwed.constant.Message;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.exception.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorController {

    @ExceptionHandler(ErrorResponse.class)
    public ResponseEntity<?> handle(ErrorResponse e) {
        ApiResponse<?> response = ApiResponse.failed(e.getMessage(), e.getError());
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handle(AuthenticationException e) {
        ApiResponse<?> response = ApiResponse.failed(Message.ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception e) {
        ApiResponse<?> response = ApiResponse.failed(Message.ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
