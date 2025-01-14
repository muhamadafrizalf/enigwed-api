package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
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
            summary = "For admin and wedding organizer to get all notification in channel SYSTEM for user [ADMIN, WO] (WEB)",
            description = "Each user can only  retrieve their own notification (order by read = false and created at)"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_NOTIFICATION)
    public ResponseEntity<?> getOwnNotification(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = notificationService.getOwnNotifications(userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin and wedding organizer to update status read to true of notification by user [ADMIN, WO] (WEB)",
            description = "Each user can only  read their own notification"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(SPathApi.PROTECTED_NOTIFICATION_ID)
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
            summary = "For development to get all notification in database (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @GetMapping(SPathApi.PUBLIC_NOTIFICATION + "/dev")
    public ResponseEntity<?> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

}
