package com.enigwed.service.impl;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.RefreshToken;
import com.enigwed.dto.request.LoginRequest;
import com.enigwed.dto.request.RegisterRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.LoginResponse;
import com.enigwed.dto.response.UserResponse;
import com.enigwed.entity.*;
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
    private final AddressService addressService;
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;
    private final ValidationUtil validationUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<LoginResponse> login(LoginRequest loginRequest) {
        try {
            /* VALIDATE INPUT */
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
            // ErrorResponse
            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .role(userCredential.getRole().name())
                    .build();
            if (userCredential.getRole() == ERole.ROLE_WO) {
                WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userCredential.getId());
                UserResponse user = UserResponse.fromUser(weddingOrganizer);
                response.setUser(user);
            }

            return ApiResponse.success(response, SMessage.LOGIN_SUCCESS);
        } catch (ValidationException e) {
            log.error("Validation error during login: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.LOGIN_FAILED, e.getErrors().get(0));
        } catch (AuthenticationException e) {
            log.error("Authentication error during login: {}", e.getMessage());
            if (e.getMessage().equals("User is disabled")) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, SMessage.LOGIN_FAILED, SErrorMessage.ACCOUNT_NOT_ACTIVE);
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, SMessage.LOGIN_FAILED, SErrorMessage.INVALID_EMAIL_OR_PASSWORD);
        } catch (JWTCreationException e) {
            log.error("JWT creation error during login: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.LOGIN_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during login: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException
            validationUtil.validateAndThrow(registerRequest);
            // ErrorResponse
            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword()))
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REGISTER_FAILED, SErrorMessage.CONFIRM_PASSWORD_MISMATCH);

            /* CREATE AND SAVE USER CREDENTIAL */
            UserCredential userCredential = UserCredential.builder()
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(ERole.ROLE_WO)
                    .build();
            // DataIntegrityViolationException
            userCredential = userCredentialService.createUser(userCredential);

            /* CREATE OR LOAD ADDRESS */
            Province province = addressService.saveOrLoadProvince(registerRequest.getProvince());
            // ErrorResponse
            Regency regency = addressService.saveOrLoadRegency(registerRequest.getRegency());
            // ErrorResponse
            District district = addressService.saveOrLoadDistrict(registerRequest.getDistrict());

            /* INITIALIZE AVATAR */
            // ErrorResponse
            Image avatar = imageService.createImage(null);

            /* CREATE WEDDING ORGANIZER */
            WeddingOrganizer wo = WeddingOrganizer.builder()
                    .name(registerRequest.getName())
                    .description(registerRequest.getDescription())
                    .address(registerRequest.getAddress())
                    .npwp(registerRequest.getNpwp())
                    .nib(registerRequest.getNib())
                    .phone(registerRequest.getPhone())
                    .province(province)
                    .regency(regency)
                    .district(district)
                    .avatar(avatar)
                    .userCredential(userCredential)
                    .build();

            /* VALIDATE DATA INTEGRITY AND SAVE WEDDING ORGANIZER */
            // DataIntegrityViolationException
            weddingOrganizerService.createWeddingOrganizer(wo);

            /* CREATE NOTIFICATION FOR ADMIN */
            Notification notification = Notification.builder()
                    .channel(ENotificationChannel.SYSTEM)
                    .type(ENotificationType.ACCOUNT_REGISTRATION)
                    .receiver(EReceiver.ADMIN)
                    .receiverId(userCredentialService.loadAdminId())
                    .dataType(EDataType.WEDDING_ORGANIZER)
                    .dataId(wo.getId())
                    .message(SMessage.NEW_ACCOUNT_REGISTRATION(wo.getName()))
                    .build();
            notificationService.createNotification(notification);

            /*

            Create notification for email [SOON]

            */

            return ApiResponse.success(SMessage.REGISTER_SUCCESS);

        } catch (ValidationException e) {
            log.error("Validation error during register: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REGISTER_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Database conflict error during register: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.REGISTER_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during register: {}", e.getError());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during register: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<RefreshToken> refresh(RefreshToken refreshToken) {
        try {
            /* VALIDATE INPUT */
            // ValidationException
            validationUtil.validateAndThrow(refreshToken);

            /* VALIDATE TOKEN */
            // ErrorResponse
            if (!jwtUtil.verifyJwtToken(refreshToken.getToken())) throw new ErrorResponse(HttpStatus.UNAUTHORIZED, SMessage.REFRESH_TOKEN_FAILED, SErrorMessage.INVALID_TOKEN);

            /* GET USER INFO */
            JwtClaim userInfo = jwtUtil.getUserInfoByToken(refreshToken.getToken());
            // ErrorResponse
            if (userInfo == null) throw new ErrorResponse(HttpStatus.UNAUTHORIZED, SMessage.REFRESH_TOKEN_FAILED, SErrorMessage.INVALID_TOKEN);

            /* LOAD USER */
            // UsernameNotFoundException
            UserCredential user = userCredentialService.loadUserById(userInfo.getUserId());

            /* CREATE NEW TOKEN */
            // JWTCreationException
            String token = jwtUtil.generateToken(user);
            RefreshToken newToken = RefreshToken.builder()
                    .token(token)
                    .build();

            return ApiResponse.success(newToken, SMessage.REFRESH_TOKEN_SUCCESS);

        } catch (ValidationException e) {
            log.error("Validation error during refresh token: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REFRESH_TOKEN_FAILED, e.getErrors().get(0));
        } catch (UsernameNotFoundException e) {
            log.error("Username not found error during refresh token: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.REFRESH_TOKEN_FAILED, e.getMessage());
        } catch (JWTCreationException e) {
            log.error("JWT creation error during refresh token: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.LOGIN_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during refresh token: {}", e.getError());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during refresh token: {}", e.getMessage());
            throw e;
        }
    }
}
