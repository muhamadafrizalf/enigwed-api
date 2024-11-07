package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.EUserStatus;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
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

    @Operation(summary = "To get wedding organizer by wedding_organizer_id (MOBILE)")
    @GetMapping(PathApi.PUBLIC_WO_ID)
    public ResponseEntity<?> customerGetWeddingOrganizerById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = weddingOrganizerService.customerFindWeddingOrganizerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To get all wedding organizers (MOBILE)")
    @GetMapping(PathApi.PUBLIC_WO)
    public ResponseEntity<?> customerGetAllWeddingOrganizers(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,

            @Parameter(description = "Keyword can filter result by name, phone, description, address, and city name", required = false)
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String provinceId,
            @RequestParam(required = false) String regencyId,
            @RequestParam(required = false) String districtId
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        FilterRequest filter = new FilterRequest();
        if (provinceId != null && !provinceId.isEmpty()) filter.setProvinceId(provinceId);
        if (regencyId != null && !regencyId.isEmpty()) filter.setRegencyId(regencyId);
        if (districtId != null && !districtId.isEmpty()) filter.setDistrictId(districtId);

        ApiResponse<?> response;
        if (keyword != null && !keyword.isEmpty()) {
            response = weddingOrganizerService.customerSearchWeddingOrganizer(keyword, filter, pagingRequest);
        } else {
            response = weddingOrganizerService.customerFindAllWeddingOrganizers(filter, pagingRequest);
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(PathApi.PROTECTED_WO_ID)
    public ResponseEntity<?> getWeddingOrganizerById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = weddingOrganizerService.findWeddingOrganizerById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get wedding organizers [ADMIN, WO] (WEB)",
            description = "Admin get all wedding organizer, WO can only get their own wedding organizer"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_WO)
    public ResponseEntity<?> getWeddingOrganizerProfile(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,

            @Parameter(description = "Keyword can filter result by name, phone, description, address, province name, city name, and district name", required = false)
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) EUserStatus status,
            @RequestParam(required = false) String provinceId,
            @RequestParam(required = false) String regencyId,
            @RequestParam(required = false) String districtId
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        FilterRequest filter = new FilterRequest();
        if (status != null) filter.setUserStatus(status);
        if (provinceId != null && !provinceId.isEmpty()) filter.setProvinceId(provinceId);
        if (regencyId != null && !regencyId.isEmpty()) filter.setRegencyId(regencyId);
        if (districtId != null && !districtId.isEmpty()) filter.setDistrictId(districtId);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = weddingOrganizerService.getOwnWeddingOrganizer(userInfo);
        } else {
            if (keyword != null && !keyword.isEmpty()) {
                response = weddingOrganizerService.searchWeddingOrganizer(keyword, filter, pagingRequest);
            } else {
                response = weddingOrganizerService.findAllWeddingOrganizers(filter, pagingRequest);
            }
        }
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
            summary = "To delete wedding organizer by wedding_organizer_id [ADMIN, WO] (WEB)",
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
            summary = "To update wedding organizer image by wedding_organizer_id [WO] (WEB)",
            description = "Only WO can update wedding organizer image and each wedding organizer can only access their own account"
    )
    @PreAuthorize("hasAnyRole('WO')")
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
            summary = "To delete wedding organizer image by wedding_organizer_id [WO] (WEB)",
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
            summary = "To activate wedding organizer account by wedding_organizer_id [ADMIN] (WEB)",
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
