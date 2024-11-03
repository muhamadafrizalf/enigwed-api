package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.WeddingOrganizerService;
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

    @GetMapping(PathApi.PUBLIC_WO_ID)
    public ResponseEntity<?> getWeddingOrganizerById(@PathVariable String id) {
        ApiResponse<?> response = weddingOrganizerService.findWeddingOrganizerById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_WO)
    public ResponseEntity<?> getAllWeddingOrganizers(
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

    @PreAuthorize("hasRole('WO')")
    public ResponseEntity<?> getOwnWeddingOrganizer(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.getOwnWeddingOrganizer(userInfo);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(PathApi.PROTECTED_WO)
    public ResponseEntity<?> updateWeddingOrganizer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingOrganizerRequest weddingOrganizerRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.updateWeddingOrganizer(userInfo, weddingOrganizerRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(PathApi.PROTECTED_WO_ID)
    public ResponseEntity<?> deleteWeddingOrganizer(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.deleteWeddingOrganizer(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(value = PathApi.PROTECTED_WO_ID_IMAGES, consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateWeddingOrganizerImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "avatar", required = true) MultipartFile avatar
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.updateWeddingOrganizerImage(userInfo, id, avatar);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WO_ID_IMAGES)
    public ResponseEntity<?> deleteWeddingOrganizerImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingOrganizerService.deleteWeddingOrganizerImage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PathApi.PROTECTED_WO_ID_ACTIVATE)
    public ResponseEntity<?> activateWeddingOrganizer(@PathVariable String id) {
        ApiResponse<?> response = weddingOrganizerService.activateWeddingOrganizer(id);
        return ResponseEntity.ok(response);
    }
}
