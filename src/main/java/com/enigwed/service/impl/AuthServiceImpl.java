package com.enigwed.service.impl;

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
            validationUtil.validateAndThrow(loginRequest);

            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));

            UserCredential userCredential = (UserCredential) authenticate.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            String token = jwtUtil.generateToken(userCredential);

            LoginResponse response = LoginResponse.builder()
                    .token(token)
                    .role(userCredential.getRole().name())
                    .build();

            return ApiResponse.success(response, Message.LOGIN_SUCCESS);
        } catch (ValidationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.LOGIN_FAILED, e.getErrors().get(0));
        } catch (AuthenticationException e) {
            if (e.getMessage().equals("Bad credentials")) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, ErrorMessage.INVALID_EMAIL_OR_PASSWORD);
            }
            if (e.getMessage().equals("User is disabled")) {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, ErrorMessage.ACCOUNT_NOT_ACTIVE);
            }
            throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.LOGIN_FAILED, e.getMessage());
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> register(RegisterRequest registerRequest) {
        try {
            validationUtil.validateAndThrow(registerRequest);

            if (!registerRequest.getPassword().equals(registerRequest.getConfirmPassword())) {
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, ErrorMessage.CONFIRM_PASSWORD_MISMATCH);
            }

            UserCredential userCredential = UserCredential.builder()
                    .email(registerRequest.getEmail())
                    .password(passwordEncoder.encode(registerRequest.getPassword()))
                    .role(ERole.ROLE_WO)
                    .build();

            userCredential = userCredentialService.create(userCredential);

            City city = cityService.loadCityById(registerRequest.getCityId());
            Image avatar = imageService.create(null);

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

            weddingOrganizerService.create(wo);

            return ApiResponse.success(Message.REGISTER_SUCCESS);
        } catch (ValidationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.REGISTER_FAILED, e.getMessage());
        }

    }

    @Override
    public ApiResponse<RefreshToken> refresh(RefreshToken refreshToken) {
        try {
            validationUtil.validateAndThrow(refreshToken);

            if (jwtUtil.verifyJwtToken(refreshToken.getToken())) {
                JwtClaim userInfo = jwtUtil.getUserInfoByToken(refreshToken.getToken());
                UserCredential user = userCredentialService.loadUserById(userInfo.getUserId());
                String token = jwtUtil.generateToken(user);

                RefreshToken newToken = RefreshToken.builder().token(token).build();

                return ApiResponse.success(newToken, Message.REFRESH_TOKEN_SUCCESS);
            } else {
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.REFRESH_TOKEN_FAILED, ErrorMessage.INVALID_TOKEN);
            }
        } catch (ValidationException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REFRESH_TOKEN_FAILED, e.getErrors().get(0));
        } catch (UsernameNotFoundException e) {
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REFRESH_TOKEN_FAILED, e.getMessage());
        }
    }
}
