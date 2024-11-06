package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {
    private final NotificationService notificationService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "To get all notification in channel SYSTEM by user [ADMIN, WO] (WEB)"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_NOTIFICATION)
    public ResponseEntity<?> getOwnNotification(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = notificationService.getOwnNotifications(userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To update status read to true of notification by user [ADMIN, WO] (WEB)",
            description = "Each user can only  read their own notification"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
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
    @Operation(
            summary = "Get all notification in database (For development only, don't use)"
    )
    @GetMapping(PathApi.PUBLIC_NOTIFICATION + "/dev")
    public ResponseEntity<?> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

}
