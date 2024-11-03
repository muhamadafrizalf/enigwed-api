package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.WeddingPackageService;
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
public class WeddingPackageController {
    private final WeddingPackageService weddingPackageService;
    private final JwtUtil jwtUtil;

    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getWeddingPackageById(@PathVariable String id) {
        ApiResponse<?> response = weddingPackageService.findWeddingPackageById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE)
    public ResponseEntity<?> getAllWeddingPackages(
            @RequestParam(required = false) String weddingOrganizerId,
            @RequestParam(required = false) String keyword
    ) {
        ApiResponse<?> response;
        boolean isWoId = weddingOrganizerId != null && !weddingOrganizerId.isEmpty();
        boolean isKeyword = keyword != null && !keyword.isEmpty();
        if (isWoId && isKeyword) {
            response = weddingPackageService.searchWeddingPackageFromWeddingOrganizerId(weddingOrganizerId, keyword);
        } else if (isWoId) {
            response = weddingPackageService.findAllWeddingPackagesByWeddingOrganizerId(weddingOrganizerId);
        } else if (isKeyword) {
            response = weddingPackageService.searchWeddingPackage(keyword);
        } else {
            response = weddingPackageService.findAllWeddingPackages();
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> getOwnWeddingPackages(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.getOwnWeddingPackages(userInfo);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> createWeddingPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.createWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> updateWeddingPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.updateWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> deleteWeddingPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE)
    public ResponseEntity<?> addWeddingPackageImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "image") MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.addWeddingPackageImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE_ID)
    public ResponseEntity<?> deleteWeddingPackageImage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackageImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }
}
