package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.Error;
import com.enigwed.constant.Message;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;
import com.enigwed.entity.UserCredential;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.AuthService;
import com.enigwed.service.UserCredentialService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserCredentialService userCredentialService;
    private final WeddingOrganizerService weddingOrganizerService;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
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

            return ApiResponse.success(response, Message.LOGIN_SUCCESS);
        } catch (AuthenticationException e) {
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, Error.INVALID_EMAIL_OR_PASSWORD);
        }

    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        try {
            validationUtil.validate(registerRequest);

            UserCredential userCredential = UserCredential.builder()
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(ERole.ROLE_WO)
                    .build();

            userCredential = userCredentialService.create(userCredential);

            WeddingOrganizer wo = WeddingOrganizer.builder()
                    .name(registerRequest.getName())
                    .description(registerRequest.getDescription())
                    .address(registerRequest.getAddress())
                    .npwp(registerRequest.getNpwp())
                    .nib(registerRequest.getNib())
                    .phone(registerRequest.getPhone())
                    .userCredential(userCredential)
                    .build();

            weddingOrganizerService.create(wo);

            return ApiResponse.success(Message.REGISTER_SUCCESS);
        } catch (ValidationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, e.getMessage());
        }

    }
}
