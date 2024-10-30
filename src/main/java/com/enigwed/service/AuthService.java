package com.enigwed.service;

import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;

public interface AuthService {
    ApiResponse<LoginResponse> login(LoginRequest loginRequest);
    ApiResponse<?> register(RegisterRequest registerRequest);
}
