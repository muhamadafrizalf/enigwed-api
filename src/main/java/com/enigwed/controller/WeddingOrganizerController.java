package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.WeddingOrganizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class WeddingOrganizerController {
    private final WeddingOrganizerService weddingOrganizerService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "To get wedding organizer by wedding_organizer_id (no authorization needed)")
    @GetMapping(PathApi.PUBLIC_WO_ID)
    public ResponseEntity<?> getWeddingOrganizerById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = weddingOrganizerService.findWeddingOrganizerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To get all wedding organizers (no authorization needed)")
    @GetMapping(PathApi.PUBLIC_WO)
    public ResponseEntity<?> getAllWeddingOrganizers(
            @Parameter(description = "Keyword can filter result by name, phone, description, address, and city name", required = false)
            @RequestParam(required = false) String keyword
    ) {
        ApiResponse<?> response;
        if (keyword != null && !keyword.isEmpty()) {
            response = weddingOrganizerService.searchWeddingOrganizer(keyword);
        } else {
            response = weddingOrganizerService.findAllWeddingOrganizers();
        }
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To get own wedding organizer (authorization WO)")
    @PreAuthorize("hasAnyRole('WO')")
    @GetMapping(PathApi.PROTECTED_WO)
    public ResponseEntity<?> getWeddingOrganizerProfile(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.getOwnWeddingOrganizer(userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To update wedding organizer by wedding_organizer_id (authorization WO)",
            description = "Only WO can update wedding organizer and each wedding organizer can only update their own account"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @PutMapping(PathApi.PROTECTED_WO)
    public ResponseEntity<?> updateWeddingOrganizer(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingOrganizerRequest weddingOrganizerRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.updateWeddingOrganizer(userInfo, weddingOrganizerRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete wedding organizer by wedding_organizer_id (authorization ADMIN and WO)",
            description = "Admin can delete all wedding organizer and each wedding organizer can only delete their own account"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WO_ID)
    public ResponseEntity<?> deleteWeddingOrganizer(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.deleteWeddingOrganizer(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To update wedding organizer image by wedding_organizer_id (authorization WO)",
            description = "Only WO can update wedding organizer image and each wedding organizer can only access their own account"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(value = PathApi.PROTECTED_WO_ID_IMAGE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateWeddingOrganizerImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "avatar", required = true) MultipartFile avatar
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.updateWeddingOrganizerImage(userInfo, id, avatar);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete wedding organizer image by wedding_organizer_id (authorization WO)",
            description = "Only WO can delete wedding organizer image and each wedding organizer can only access their own account"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @DeleteMapping(PathApi.PROTECTED_WO_ID_IMAGE)
    public ResponseEntity<?> deleteWeddingOrganizerImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.deleteWeddingOrganizerImage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To activate wedding organizer account by wedding_organizer_id (authorization WO)",
            description = "Only ADMIN can activate wedding organizer account"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PathApi.PROTECTED_WO_ID_ACTIVATE)
    public ResponseEntity<?> activateWeddingOrganizer(
            @PathVariable String id
    ) {
        ApiResponse<?> response = weddingOrganizerService.activateWeddingOrganizer(id);
        return ResponseEntity.ok(response);
    }
}
