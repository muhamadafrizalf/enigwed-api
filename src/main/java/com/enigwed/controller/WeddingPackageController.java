package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
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
            summary = "To get wedding package by wedding_package_id (MOBILE)"
    )
    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getWeddingPackageById(@PathVariable String id) {
        ApiResponse<?> response = weddingPackageService.customerFindWeddingPackageById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all wedding packages (MOBILE)",
            description = "With filter wedding organizer id, province, regency, min and max price, and keyword"
    )
    @GetMapping(PathApi.PUBLIC_WEDDING_PACKAGE)
    public ResponseEntity<?> customerGetAllWeddingPackages(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @Parameter(description = "Keyword can filter result by name, description, city name, and wedding organizer name", required = false)
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String weddingOrganizerId,
            @RequestParam(required = false) String provinceId,
            @RequestParam(required = false) String regencyId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);

        FilterRequest filter = new FilterRequest();
        if (weddingOrganizerId != null && !weddingOrganizerId.isEmpty()) filter.setWeddingOrganizerId(weddingOrganizerId);
        if (provinceId != null && !provinceId.isEmpty()) filter.setProvinceId(provinceId);
        if (regencyId != null && !regencyId.isEmpty()) filter.setRegencyId(regencyId);
        if (minPrice != null) filter.setMinPrice(minPrice);
        if (maxPrice != null) filter.setMaxPrice(maxPrice);

        ApiResponse<?> response;
        if (keyword != null && !keyword.isEmpty()) {
            response = weddingPackageService.customerSearchWeddingPackage(keyword, filter, pagingRequest);
        } else {
            response = weddingPackageService.customerFindAllWeddingPackages(filter, pagingRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get wedding package by wedding_package_id [ADMIN, WO] (WEB)",
            description = "Admin can get access to all wedding package, each WO can only access their own wedding package"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getOwnWeddingPackageById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = weddingPackageService.getOwnWeddingPackageById(userInfo, id);
        } else {
            response = weddingPackageService.findWeddingPackageById(id);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all wedding packages [ADMIN, WO] (WEB)",
            description = "Admin get all wedding packages, WO can only get their own wedding packages, can be filter"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> getOwnWeddingPackages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,

            @Parameter(description = "Keyword can filter result by name, description, and city name", required = false)
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String weddingOrganizerId,
            @RequestParam(required = false) String provinceId,
            @RequestParam(required = false) String regencyId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);

        FilterRequest filter = new FilterRequest();
        if (weddingOrganizerId != null && !weddingOrganizerId.isEmpty()) filter.setWeddingOrganizerId(weddingOrganizerId);
        if (provinceId != null && !provinceId.isEmpty()) filter.setProvinceId(provinceId);
        if (regencyId != null && !regencyId.isEmpty()) filter.setRegencyId(regencyId);
        if (minPrice != null) filter.setMinPrice(minPrice);
        if (maxPrice != null) filter.setMaxPrice(maxPrice);

        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            if (keyword != null && !keyword.isEmpty()) {
                response = weddingPackageService.searchOwnWeddingPackages(userInfo, keyword, filter, pagingRequest);
            } else {
                response = weddingPackageService.getOwnWeddingPackages(userInfo, filter, pagingRequest);
            }
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                response = weddingPackageService.searchWeddingPackage(keyword, filter, pagingRequest);
            } else {
                response = weddingPackageService.findAllWeddingPackages(filter, pagingRequest);
            }
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To create wedding package [WO] (WEB)",
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
            summary = "To update wedding package [WO] (WEB)",
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
            summary = "To delete wedding package by wedding_package_id [WO] (WEB)",
            description = "Only wedding organizer can delete wedding package and wedding organizer can only delete their own wedding package"
    )
    @PreAuthorize("hasAnyRole('WO')")
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
            summary = "To add wedding package image [WO] (WEB)",
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
            summary = "To delete wedding package image by wedding_package_id and image_id [WO] (WEB)",
            description = "Only WO can delete wedding package images and wedding organizer can only delete their own wedding package images"
    )
    @PreAuthorize("hasAnyRole('WO')")
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
