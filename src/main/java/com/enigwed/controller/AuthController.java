package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(PathApi.AUTH)
public class AuthController {
    private final AuthService authService;

    @PostMapping(PathApi.LOGIN)
    public ResponseEntity<?> login (@RequestBody LoginRequest loginRequest) {
        ApiResponse<?> apiResponse = authService.login(loginRequest);
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping(PathApi.RESISTER)
    public ResponseEntity<?> register (@RequestBody RegisterRequest registerRequest) {
        ApiResponse<?> apiResponse = authService.register(registerRequest);
        return ResponseEntity.ok(apiResponse);
    }
}
