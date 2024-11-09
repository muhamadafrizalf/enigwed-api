package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.constant.SPathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPacketRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    @Operation(
            summary = "For admin and wedding organizer to get list of subscription packages [ADMIN, WO] (WEB)"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> getSubscriptionPrice() {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPrices());
    }

    @Operation(
            summary = "For admin and wedding organizer to get subscription package information by subscription_package_id [ADMIN, WO] (WEB)"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_SUBSCRIPTION_PRICE_ID)
    public ResponseEntity<?> getSubscriptionPriceId(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPriceById(id));
    }

    @Operation(
            summary = "For admin to create new subscription package [ADMIN] (WEB)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(SPathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> addSubscriptionPrice(
            @RequestBody SubscriptionPacketRequest subscriptionPacketRequest
    ) {
        return ResponseEntity.ok(subscriptionService.addSubscriptionPrice(subscriptionPacketRequest));
    }

    @Operation(
            summary = "For admin to update existing subscription package [ADMIN] (WEB)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(SPathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> updateSubscriptionPrice(
            @RequestBody SubscriptionPacketRequest subscriptionPacketRequest
    ) {
        return ResponseEntity.ok(subscriptionService.updateSubscriptionPrice(subscriptionPacketRequest));
    }

    @Operation(
            summary = "For admin to delete subscription package by subscription_package_id [ADMIN] (WEB)"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(SPathApi.PROTECTED_SUBSCRIPTION_PRICE_ID)
    public ResponseEntity<?> deleteSubscriptionPrice(@PathVariable String id) {
        return ResponseEntity.ok(subscriptionService.deleteSubscriptionPrice(id));
    }

    @Operation(
            summary = "For wedding organizer to upload payment image of subscription [WO] (WEB)",
            description = "Receive form-data String subscriptionPriceId (subscription package id) and MultipartFile paymentImage (payment image)"
    )
    @PreAuthorize("hasRole('WO')")
    @PostMapping(value = SPathApi.PROTECTED_SUBSCRIPTION, consumes = "multipart/form-data")
    public ResponseEntity<?> paySubscription(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestPart String subscriptionPriceId,
            @RequestPart MultipartFile paymentImage
    ) {
        SubscriptionRequest subscriptionRequest = SubscriptionRequest.builder()
                .subscriptionPriceId(subscriptionPriceId)
                .paymentImage(paymentImage)
                .build();
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = subscriptionService.paySubscription(userInfo, subscriptionRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin and wedding organizer to get list of subscription invoice (Default pagination {page:1, size:8}) [ADMIN, WO] (WEB)",
            description = "Admin can retrieve all subscription invoice, wedding organizer can only get their own invoice"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_SUBSCRIPTION)
    public ResponseEntity<?> getSubscriptions(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @RequestParam(required = false) ESubscriptionPaymentStatus status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);

        FilterRequest filterRequest = new FilterRequest();
        if (status != null) filterRequest.setSubscriptionPaymentStatus(status);
        if (startDate != null) filterRequest.setStartDate(startDate);
        if (endDate != null) filterRequest.setEndDate(endDate);

        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = subscriptionService.getOwnSubscriptions(userInfo, filterRequest, pagingRequest);
        } else {
            response = subscriptionService.getAllSubscriptions(pagingRequest, filterRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to get list of active subscription they own (Default pagination {page:1, size:8}) [WO] (WEB)"
    )
    @PreAuthorize("hasRole('WO')")
    @GetMapping(SPathApi.PROTECTED_SUBSCRIPTION_ACTIVE)
    public ResponseEntity<?> getActiveSubscription(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = subscriptionService.getActiveSubscriptions(userInfo, pagingRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin and wedding organizer to get subscription invoice information by subscription_id [ADMIN, WO] (WEB)",
            description = "Admin can get subscription invoice, wedding organizer can only get their own invoice"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(SPathApi.PROTECTED_SUBSCRIPTION_ID)
    public ResponseEntity<?> getSubscriptionById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = subscriptionService.getSubscriptionById(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For admin to confirm subscription payment subscription_id [ADMIN] (WEB)",
            description = "Extend user active until duration"
    )
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(SPathApi.PROTECTED_SUBSCRIPTION_ID)
    public ResponseEntity<?> confirmSubscriptionById(@PathVariable String id) {
        ApiResponse<?> response = subscriptionService.confirmPaymentSubscriptionById(id);
        return ResponseEntity.ok(response);
    }
}
