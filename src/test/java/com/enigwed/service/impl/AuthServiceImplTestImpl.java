package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.Message;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;
import com.enigwed.entity.UserCredential;
import com.enigwed.exception.ValidationException;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceImplTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private WeddingOrganizerService weddingOrganizerService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        loginRequest = new LoginRequest("test@example.com", "password123");
        registerRequest = new RegisterRequest("Name", "Description", "Address", "npwp", "nib", "city_id", "phone", "email@google.com", "password", "password");
    }

//    @Test
//    void login_ShouldReturnLoginResponse_WhenValidRequest() {
//        // Arrange
//        doNothing().when(validationUtil).validateAndThrow(loginRequest);
//
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        when(authentication.getPrincipal()).thenReturn(UserCredential.builder().email("test@example.com").password("password123").role(ERole.ROLE_WO).isActive(true).build());
//        when(jwtUtil.generateToken(any(UserCredential.class))).thenReturn("generatedToken");
//
//        // Act
//        ApiResponse<LoginResponse> response = authService.login(loginRequest);
//
//        // Assert
//        assertEquals("generatedToken", response.getData().getToken());
//        assertEquals("ROLE_WO", response.getData().getRole());
//        assertEquals(Message.LOGIN_SUCCESS, response.getMessage());
//    }

//    @Test
//    void login_ShouldThrowErrorResponse_WhenValidationFails() {
//        // Arrange
//        when(validationUtil.validateAndThrow(loginRequest)).thenThrow(new ValidationException("Validation error"));
//
//        // Act & Assert
//        ErrorResponse exception = assertThrows(ErrorResponse.class, () -> authService.login(loginRequest));
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
//        assertEquals(Message.LOGIN_FAILED, exception.getMessage());
//    }
//
//    @Test
//    void register_ShouldReturnSuccess_WhenValidRequest() {
//        // Arrange
//        when(validationUtil.validateAndThrow(registerRequest)).thenReturn(null);
//        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
//
//        UserCredential userCredential = new UserCredential("test@example.com", "encodedPassword", ERole.ROLE_WO);
//        when(userCredentialService.create(any(UserCredential.class))).thenReturn(userCredential);
//
//        WeddingOrganizer weddingOrganizer = new WeddingOrganizer();
//        when(weddingOrganizerService.create(any(WeddingOrganizer.class))).thenReturn(weddingOrganizer);
//
//        // Act
//        ApiResponse<?> response = authService.register(registerRequest);
//
//        // Assert
//        assertEquals(Message.REGISTER_SUCCESS, response.getMessage());
//    }
//
//    @Test
//    void register_ShouldThrowErrorResponse_WhenPasswordMismatch() {
//        // Arrange
//        registerRequest.setConfirmPassword("differentPassword");
//
//        // Act & Assert
//        ErrorResponse exception = assertThrows(ErrorResponse.class, () -> authService.register(registerRequest));
//        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
//        assertEquals(Message.REGISTER_FAILED, exception.getMessage());
//    }
//
//    @Test
//    void refresh_ShouldReturnNewToken_WhenValidToken() {
//        // Arrange
//        RefreshToken refreshToken = new RefreshToken("validToken");
//        when(validationUtil.validateAndThrow(refreshToken)).thenReturn(null);
//        when(jwtUtil.verifyJwtToken(refreshToken.getToken())).thenReturn(true);
//        JwtClaim userInfo = new JwtClaim();
//        when(jwtUtil.getUserInfoByToken(refreshToken.getToken())).thenReturn(userInfo);
//        UserCredential userCredential = new UserCredential("test@example.com", "password123", ERole.ROLE_WO);
//        when(userCredentialService.loadUserById(userInfo.getUserId())).thenReturn(userCredential);
//        when(jwtUtil.generateToken(userCredential)).thenReturn("newGeneratedToken");
//
//        // Act
//        ApiResponse<RefreshToken> response = authService.refresh(refreshToken);
//
//        // Assert
//        assertEquals("newGeneratedToken", response.getData().getToken());
//        assertEquals(Message.REFRESH_TOKEN_SUCCESS, response.getMessage());
//    }
//
//    @Test
//    void refresh_ShouldThrowErrorResponse_WhenTokenInvalid() {
//        // Arrange
//        RefreshToken refreshToken = new RefreshToken("invalidToken");
//        when(validationUtil.validateAndThrow(refreshToken)).thenReturn(null);
//        when(jwtUtil.verifyJwtToken(refreshToken.getToken())).thenReturn(false);
//
//        // Act & Assert
//        ErrorResponse exception = assertThrows(ErrorResponse.class, () -> authService.refresh(refreshToken));
//        assertEquals(HttpStatus.UNAUTHORIZED, exception.getHttpStatus());
//        assertEquals(Message.REFRESH_TOKEN_FAILED, exception.getMessage());
//    }
}