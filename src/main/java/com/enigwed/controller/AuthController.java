package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
import com.enigwed.dto.RefreshToken;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(SPathApi.AUTH)
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Login to get token and other addition info")
    @PostMapping(SPathApi.LOGIN)
    public ResponseEntity<?> login (
            @RequestBody LoginRequest loginRequest
    ) {
        ApiResponse<?> apiResponse = authService.login(loginRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "Register to create new wedding organizer account")
    @PostMapping(SPathApi.REGISTER)
    public ResponseEntity<?> register (
            @RequestBody RegisterRequest registerRequest
    ) {
        ApiResponse<?> apiResponse = authService.register(registerRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @Operation(summary = "To get new token by sending active old token")
    @PostMapping(SPathApi.REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken (
            @RequestBody RefreshToken refreshToken
    ) {
        ApiResponse<?> response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}
