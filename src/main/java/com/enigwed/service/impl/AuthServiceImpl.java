package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;
import com.enigwed.entity.UserCredential;
import com.enigwed.repository.UserCredentialRepository;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.AuthService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialRepository userCredentialRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Value("${com.enigwed.emaail-admin}")
    private String emailAdmin;

    @Value("${com.enigwed.password-admin}")
    private String passwordAdmin;

    @PostConstruct
    public void initAdmin() {
        if (userCredentialRepository.findByEmailAndDeletedAtIsNull(emailAdmin).isPresent()) return;

        UserCredential admin = UserCredential.builder()
                .email(emailAdmin)
                .password(passwordEncoder.encode(passwordAdmin))
                .role(ERole.ROLE_ADMIN)
                .isActive(true)
                .build();

        userCredentialRepository.save(admin);
    }

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        UserCredential userCredential = (UserCredential) authenticate.getPrincipal();
        String token = jwtUtil.generateToken(userCredential);

        LoginResponse response = LoginResponse.builder()
                .token(token)
                .role(userCredential.getRole().name())
                .build();

        return ApiResponse.<LoginResponse>builder()
                .message("Login success")
                .data(response)
                .build();
    }

    @Override
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        return null;
    }
}
