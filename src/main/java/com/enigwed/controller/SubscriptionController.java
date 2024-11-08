package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPriceRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final JwtUtil jwtUtil;

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> getSubscriptionPrice() {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPrices());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_SUBSCRIPTION_PRICE_ID)
    public ResponseEntity<?> getSubscriptionPriceId(
            @PathVariable String id
    ) {
        return ResponseEntity.ok(subscriptionService.getSubscriptionPriceById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(PathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> addSubscriptionPrice(
            @RequestBody SubscriptionPriceRequest subscriptionPriceRequest
    ) {
        return ResponseEntity.ok(subscriptionService.addSubscriptionPrice(subscriptionPriceRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PathApi.PROTECTED_SUBSCRIPTION_PRICE)
    public ResponseEntity<?> updateSubscriptionPrice(
            @RequestBody SubscriptionPriceRequest subscriptionPriceRequest
    ) {
        return ResponseEntity.ok(subscriptionService.updateSubscriptionPrice(subscriptionPriceRequest));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(PathApi.PROTECTED_SUBSCRIPTION_PRICE_ID)
    public ResponseEntity<?> deleteSubscriptionPrice(@PathVariable String id) {
        return ResponseEntity.ok(subscriptionService.deleteSubscriptionPrice(id));
    }

    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_SUBSCRIPTION)
    public ResponseEntity<?> paySubscription(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody SubscriptionRequest subscriptionRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = subscriptionService.paySubscription(userInfo, subscriptionRequest);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_SUBSCRIPTION)
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

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_SUBSCRIPTION_ID)
    public ResponseEntity<?> getSubscriptionById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = subscriptionService.getSubscriptionById(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PathApi.PROTECTED_SUBSCRIPTION_ID)
    public ResponseEntity<?> confirmSubscriptionById(@PathVariable String id) {
        ApiResponse<?> response = subscriptionService.confirmPaymentSubscriptionById(id);
        return ResponseEntity.ok(response);
    }
}
