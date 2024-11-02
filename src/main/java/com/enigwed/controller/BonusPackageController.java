package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.BonusPackageService;
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

    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_BONUS_PACKAGE)
    public ResponseEntity<?> createBonusPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BonusPackageRequest bonusPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.createBonusPackage(userInfo, bonusPackageRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_BONUS_PACKAGE_ID)
    public ResponseEntity<?> getBonusPackageById(@PathVariable String id) {
        ApiResponse<?> response = bonusPackageService.findBonusPackageById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_BONUS_PACKAGE)
    public ResponseEntity<?> getAllBonusPackages(
            @RequestParam(required = false) String keyword
    ) {
        ApiResponse<?> response;
        if (keyword != null && !keyword.isEmpty()) {
            response = bonusPackageService.searchBonusPackage(keyword);
        } else {
            response = bonusPackageService.findAllBonusPackages();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_BONUS_PACKAGE_WO_ID)
    public ResponseEntity<?> getAllBonusPackagesByWeddingOrganizerId(
            @PathVariable(value = "wedding-organizer-id") String weddingOrganizerId
    ) {
        ApiResponse<?> response = bonusPackageService.findAllBonusPackagesByWeddingOrganizerId(weddingOrganizerId);
        return ResponseEntity.ok(response);
    }

    @PutMapping(PathApi.PROTECTED_BONUS_PACKAGE)
    public ResponseEntity<?> updateBonusPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody BonusPackageRequest bonusPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.updateBonusPackage(userInfo, bonusPackageRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @DeleteMapping(PathApi.PROTECTED_BONUS_PACKAGE_ID)
    public ResponseEntity<?> deleteBonusPackage(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.deleteBonusPackage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @PutMapping(value = PathApi.PROTECTED_BONUS_PACKAGE_ID_IMAGES, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addBonusPackageImages(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "image", required = true) MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.addBonusPackageImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(PathApi.PROTECTED_BONUS_PACKAGE_ID_IMAGES_ID)
    public ResponseEntity<?> deleteBonusPackageImages(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = bonusPackageService.deleteBonusPackageImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }
}
