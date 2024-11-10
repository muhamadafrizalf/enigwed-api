package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.SPathApi;
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
            summary = "For customer to get list of wedding packages (Default pagination {page:1, size:8}) (MOBILE)",
            description = "With filter wedding organizer id, province, regency, min and max price, and keyword"
    )
    @GetMapping(SPathApi.PUBLIC_WEDDING_PACKAGE)
    public ResponseEntity<?> customerGetAllWeddingPackages(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @Parameter(description = "Keyword can filter result by name, description, province name, regency name, and wedding organizer name", required = false)
            @RequestParam(required = false) String keyword,
            @Parameter(description = "To filter by wedding_organizer_id", required = false)
            @RequestParam(required = false) String weddingOrganizerId,
            @Parameter(description = "To filter by province_id", required = false)
            @RequestParam(required = false) String provinceId,
            @Parameter(description = "To filter by regency_id", required = false)
            @RequestParam(required = false) String regencyId,
            @Parameter(description = "To filter by min_price", required = false)
            @RequestParam(required = false) Double minPrice,
            @Parameter(description = "To filter by max_price", required = false)
            @RequestParam(required = false) Double maxPrice
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);

        FilterRequest filter = new FilterRequest();
        if (weddingOrganizerId != null && !weddingOrganizerId.isEmpty()) filter.setWeddingOrganizerId(weddingOrganizerId);
        if (provinceId != null && !provinceId.isEmpty()) filter.setProvinceId(provinceId);
        if (regencyId != null && !regencyId.isEmpty()) filter.setRegencyId(regencyId);
        if (minPrice != null) filter.setMinPrice(minPrice);
        if (maxPrice != null) filter.setMaxPrice(maxPrice);

        ApiResponse<?> response = weddingPackageService.customerFindAllWeddingPackages(filter, pagingRequest, keyword);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For customer to get wedding package information by wedding_package_id (MOBILE)"
    )
    @GetMapping(SPathApi.PUBLIC_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getWeddingPackageById(
            @Parameter(description = "Path variable id")
            @PathVariable String id
    ) {
        ApiResponse<?> response = weddingPackageService.customerFindWeddingPackageById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin and wedding organizer to get list of wedding packages (Default pagination {page:1, size:8}) [ADMIN, WO] (WEB)",
            description = "Admin get all wedding packages, WO can only get their own wedding packages, can be filter"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> getOwnWeddingPackages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @Parameter(description = "Keyword can filter result by name, description, province name, regency name, and wedding organizer name", required = false)
            @RequestParam(required = false) String keyword,
            @Parameter(description = "To filter by wedding_organizer_id", required = false)
            @RequestParam(required = false) String weddingOrganizerId,
            @Parameter(description = "To filter by province_id", required = false)
            @RequestParam(required = false) String provinceId,
            @Parameter(description = "To filter by regency_id", required = false)
            @RequestParam(required = false) String regencyId,
            @Parameter(description = "To filter by min_price", required = false)
            @RequestParam(required = false) Double minPrice,
            @Parameter(description = "To filter by max_price", required = false)
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
        if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            response = weddingPackageService.findAllWeddingPackages(filter, pagingRequest, keyword);
        } else {
            response = weddingPackageService.findOwnWeddingPackages(userInfo, filter, pagingRequest, keyword);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin and wedding organizer to get wedding package information by wedding_package_id [ADMIN, WO] (WEB)",
            description = "Admin can get access to all wedding package, each WO can only access their own wedding package"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> getOwnWeddingPackageById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Path variable id")
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = weddingPackageService.findOwnWeddingPackageById(userInfo, id);
        } else {
            response = weddingPackageService.findWeddingPackageById(id);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to create wedding package [WO] (WEB)"
    )
    @PreAuthorize("hasRole('WO')")
    @PostMapping(SPathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> createWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Bonus details are optional")
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.createWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to update wedding package [WO] (WEB)",
            description = "Wedding organizer can only update their own wedding package"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(SPathApi.PROTECTED_WEDDING_PACKAGE)
    public ResponseEntity<?> updateWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Bonus details are optional")
            @RequestBody WeddingPackageRequest weddingPackageRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.updateWeddingPackage(userInfo, weddingPackageRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to delete wedding package by wedding_package_id [WO] (WEB)",
            description = "Wedding organizer can only delete their own wedding package"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @DeleteMapping(SPathApi.PROTECTED_WEDDING_PACKAGE_ID)
    public ResponseEntity<?> deleteWeddingPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Path variable id")
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackage(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to add wedding package image [WO] (WEB)",
            description = "Wedding organizer can only update their own wedding package"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(value = SPathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addWeddingPackageImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Path variable id")
            @PathVariable String id,
            @Parameter(description = "Form-data part image is required")
            @RequestPart(name = "image") MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.addWeddingPackageImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to delete wedding package image by wedding_package_id and image_id [WO] (WEB)",
            description = "Wedding organizer can only delete their own wedding package images"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @DeleteMapping(SPathApi.PROTECTED_WEDDING_PACKAGE_ID_IMAGE_ID)
    public ResponseEntity<?> deleteWeddingPackageImage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @Parameter(description = "Path variable id")
            @PathVariable String id,
            @Parameter(description = "Path variable imageId")
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = weddingPackageService.deleteWeddingPackageImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }
}
