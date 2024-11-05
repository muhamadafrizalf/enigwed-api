package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.BonusPackageService;
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
public class BonusPackageController {
    private final BonusPackageService bonusPackageService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "To get bonus package by bonus_package_id (no authorization needed)")
    @GetMapping(PathApi.PUBLIC_BONUS_PACKAGE_ID)
    public ResponseEntity<?> getBonusPackageById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = bonusPackageService.findBonusPackageById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all bonus packages, it receive wedding_organizer_id and keyword as param (no authorization needed)",
            description = "wedding_organizer_id and keyword can both be empty and filled"
    )
    @GetMapping(PathApi.PUBLIC_BONUS_PACKAGE)
    public ResponseEntity<?> getAllBonusPackages(
            @Parameter(description = "To filter by wedding_organizer_id", required = false)
            @RequestParam(required = false) String weddingOrganizerId,
            @Parameter(description = "Keyword can filter result by name, and description", required = false)
            @RequestParam(required = false) String keyword
    ) {
        ApiResponse<?> response;
        boolean isWoId = weddingOrganizerId != null && !weddingOrganizerId.isEmpty();
        boolean isKeyword = keyword != null && !keyword.isEmpty();
        if (isWoId && isKeyword) {
            response = bonusPackageService.searchBonusPackageFromWeddingOrganizerId(weddingOrganizerId, keyword);
        } else if (isWoId) {
            response = bonusPackageService.findAllBonusPackagesByWeddingOrganizerId(weddingOrganizerId);
        } else if (isKeyword) {
            response = bonusPackageService.searchBonusPackage(keyword);
        } else {
            response = bonusPackageService.findAllBonusPackages();
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all bonus packages own by wedding organizer (authorization WO)",
            description = "WO can only receive their own bonus packages"
    )
    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_BONUS_PACKAGE)
    public ResponseEntity<?> getOwnBonusPackages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.getOwnWeddingPackages(userInfo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To create new bonus package (authorization WO)")
    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_BONUS_PACKAGE)
    public ResponseEntity<?> createBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BonusPackageRequest bonusPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.createBonusPackage(userInfo, bonusPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To update existing bonus package (authorization WO)",
            description = "WO can only update their own bonus package"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @PutMapping(PathApi.PROTECTED_BONUS_PACKAGE)
    public ResponseEntity<?> updateBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BonusPackageRequest bonusPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.updateBonusPackage(userInfo, bonusPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete bonus package by bonus_package_id (authorization ADMIN and WO)",
            description = "Admin can delete all bonus package and WO can only update their own bonus package"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_BONUS_PACKAGE_ID)
    public ResponseEntity<?> deleteBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.deleteBonusPackage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete bonus package by bonus_package_id (authorization WO)",
            description = "WO can only add image to their own bonus package"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @PutMapping(value = PathApi.PROTECTED_BONUS_PACKAGE_ID_IMAGE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addBonusPackageImages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "image") MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.addBonusPackageImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To delete bonus package by bonus_package_id (authorization ADMIN and WO)",
            description = "Admin can delete all bonus package images WO can only delete image from their own bonus package"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_BONUS_PACKAGE_ID_IMAGE_ID)
    public ResponseEntity<?> deleteBonusPackageImages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.deleteBonusPackageImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }
}
