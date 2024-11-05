package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.NotificationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @GetMapping(PathApi.PROTECTED_NOTIFICATION)
    public ResponseEntity<?> getOwnNotification(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = notificationService.getOwnNotifications(userInfo);
        return ResponseEntity.ok(response);
    }

    @PutMapping(PathApi.PROTECTED_NOTIFICATION_ID)
    public ResponseEntity<?> readNotification(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = notificationService.readNotification(userInfo, id);
        return ResponseEntity.ok(response);
    }

    // For Development Use
    @GetMapping(name = "For development only, don't use.", path = PathApi.PROTECTED_NOTIFICATION + "/all")
    public ResponseEntity<?> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

}