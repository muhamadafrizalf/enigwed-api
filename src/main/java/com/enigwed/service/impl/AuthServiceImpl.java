package com.enigwed.service.impl;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.enigwed.constant.ERole;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.RefreshToken;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;
import com.enigwed.entity.City;
import com.enigwed.entity.Image;
import com.enigwed.entity.UserCredential;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.*;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {
    private final UserCredentialService userCredentialService;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ImageService imageService;
    private final CityService cityService;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(loginRequest);
            // AuthenticationException
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            UserCredential userCredential = (UserCredential) authenticate.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            // JWTCreationException
            String token = jwtUtil.generateToken(userCredential);
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .role(userCredential.getRole().name())
                    .build();
            return ApiResponse.success(response, Message.LOGIN_SUCCESS);
        } catch (ValidationException e) {
            log.error("Validation error during login: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.LOGIN_FAILED, e.getErrors().get(0));
        } catch (AuthenticationException e) {
            log.error("Authentication error during login: {}", e.getMessage());
            if (e.getMessage().equals("User is disabled")) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, ErrorMessage.ACCOUNT_NOT_ACTIVE);
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, ErrorMessage.INVALID_EMAIL_OR_PASSWORD);
        } catch (JWTCreationException e) {
            log.error("JWT creation error during login: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.LOGIN_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(registerRequest);
            // ErrorResponse (Don't catch)
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, ErrorMessage.CONFIRM_PASSWORD_MISMATCH);
            UserCredential userCredential = UserCredential.builder()
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(ERole.ROLE_WO)
                    .build();
            // DataIntegrityViolationException
            userCredential = userCredentialService.createUser(userCredential);
            // ErrorResponse (Don't catch)
            City city = cityService.loadCityById(registerRequest.getCityId());
            // ErrorResponse (Don't catch)
            Image avatar = imageService.createImage(null);
            WeddingOrganizer wo = WeddingOrganizer.builder()
                    .name(registerRequest.getName())
                    .description(registerRequest.getDescription())
                    .address(registerRequest.getAddress())
                    .npwp(registerRequest.getNpwp())
                    .nib(registerRequest.getNib())
                    .phone(registerRequest.getPhone())
                    .city(city)
                    .avatar(avatar)
                    .userCredential(userCredential)
                    .build();
            // DataIntegrityViolationException
            weddingOrganizerService.createWeddingOrganizer(wo);
            return ApiResponse.success(Message.REGISTER_SUCCESS);
        } catch (ValidationException e) {
            log.error("Validation error during register: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Database conflict error during register: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.REGISTER_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<RefreshToken> refresh(RefreshToken refreshToken) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(refreshToken);
            if (jwtUtil.verifyJwtToken(refreshToken.getToken())) {
                JwtClaim userInfo = jwtUtil.getUserInfoByToken(refreshToken.getToken());
                // ErrorResponse (Don't catch)
                if (userInfo == null) throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.REFRESH_TOKEN_FAILED, ErrorMessage.INVALID_TOKEN);
                // UsernameNotFoundException
                UserCredential user = userCredentialService.loadUserById(userInfo.getUserId());
                // JWTCreationException
                String token = jwtUtil.generateToken(user);
                RefreshToken newToken = RefreshToken.builder().token(token).build();
                return ApiResponse.success(newToken, Message.REFRESH_TOKEN_SUCCESS);
            } else {
                // ErrorResponse (Don't catch)
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.REFRESH_TOKEN_FAILED, ErrorMessage.INVALID_TOKEN);
            }
        } catch (ValidationException e) {
            log.error("Validation error during refresh token: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REFRESH_TOKEN_FAILED, e.getErrors().get(0));
        } catch (UsernameNotFoundException e) {
            log.error("Username not found error during refresh token: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REFRESH_TOKEN_FAILED, e.getMessage());
        } catch (JWTCreationException e) {
            log.error("JWT creation error during refresh token: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.LOGIN_FAILED, e.getMessage());
        }
    }
}
