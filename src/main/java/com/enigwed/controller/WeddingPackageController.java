package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.WeddingPackageService;
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
public class WeddingPackageController {
    private final WeddingPackageService weddingPackageService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "To get wedding package by wedding_package_id (no authorization needed)"
    )
    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getWeddingPackageById(@PathVariable String id) {
        ApiResponse<?> response = weddingPackageService.findWeddingPackageById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all wedding packages (no authorization needed)",
            description = "wedding_organizer_id and keyword can both be empty and filled"
    )
    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE)
    public ResponseEntity<?> getAllWeddingPackages(
            @Parameter(description = "To filter by wedding_organizer_id", required = false)
            @RequestParam(required = false) String weddingOrganizerId,
            @Parameter(description = "Keyword can filter result by name, description, city name, and wedding organizer name", required = false)
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

    @Operation(
            summary = "To get all wedding packages own by wedding organizer (authorization WO)",
            description = "keyword can both be empty and filled to filter"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @GetMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> getOwnWeddingPackages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Keyword can filter result by name, description, and city name", required = false)
            @RequestParam(required = false) String keyword
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.getOwnWeddingPackages(userInfo, keyword);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To create wedding package (authorization WO)",
            description = "Only wedding organizer can create wedding package"
    )
    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> createWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.createWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To update wedding package (authorization WO)",
            description = "Only wedding organizer can update wedding package and wedding organizer can only update their own wedding package"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> updateWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.updateWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete wedding package by wedding_package_id (authorization ADMIN and WO)",
            description = "Admin can delete all wedding packages and wedding organizer can only delete their own wedding package"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> deleteWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To add wedding package image (authorization WO)",
            description = "Only wedding organizer can add wedding package image and wedding organizer can only update their own wedding package"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(value = PathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addWeddingPackageImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "image") MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.addWeddingPackageImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete wedding package image by wedding_package_id and image_id (authorization ADMIN and WO)",
            description = "Admin can delete all wedding package images and wedding organizer can only delete their own wedding package images"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE_ID)
    public ResponseEntity<?> deleteWeddingPackageImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackageImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }
}
