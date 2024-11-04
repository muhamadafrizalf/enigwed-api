package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.EStatus;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.OrderRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    // Customer
    @PostMapping(PathApi.PUBLIC_ORDER)
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        ApiResponse<?> response = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_ORDER)
    public ResponseEntity<?> getOrderByBookCode(
            @RequestParam String bookCode
    ) {
        ApiResponse<?> response = orderService.findOrderByBookCode(bookCode);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = PathApi.PUBLIC_ORDER_ID_PAY, consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateOrder(
            @PathVariable String id,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        ApiResponse<?> response = orderService.payOrder(image, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping(PathApi.PUBLIC_ORDER_ID_CANCEL)
    public ResponseEntity<?> cancelOrder(
            @PathVariable String id
    ) {
        ApiResponse<?> response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping(PathApi.PUBLIC_ORDER_ID_FINISH)
    public ResponseEntity<?> finishOrder(
            @PathVariable String id
            /*
                ADD REVIEW HERE
             */
    ) {
        ApiResponse<?> response = orderService.finishOrder(id);
        return ResponseEntity.ok(response);
    }

    // Admin
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_ORDER_ID)
    public ResponseEntity<?> getOrderById(@PathVariable String id) {
        ApiResponse<?> response = orderService.findOrderById(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_CONFIRM)
    public ResponseEntity<?> confirmPayment(@PathVariable String id) {
        ApiResponse<?> response = orderService.confirmPayment(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_ORDER)
    public ResponseEntity<?> getAllOrders(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false) String weddingOrganizerId,
            @RequestParam(required = false) String weddingPackageId,
            @RequestParam(required = false) EStatus status,
            @RequestParam(required = false) LocalDateTime start,
            @RequestParam(required = false) LocalDateTime end
            ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        boolean isWeddingOrganizerId = weddingOrganizerId != null && !weddingOrganizerId.isEmpty();
        boolean isWeddingPackageId = weddingPackageId != null && !weddingPackageId.isEmpty();
        boolean isStatus = status != null;
        boolean isStart = start != null;
        boolean isEnd = end != null;
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            if (isWeddingPackageId) {
                response = orderService.findOwnOrdersByWeddingPackageId(userInfo, weddingPackageId);
            } else if (isStatus) {
                response = orderService.findOwnOrdersByStatus(userInfo, status);
            } else if (isStart && isEnd) {
                response = orderService.findOwnOrdersByTransactionDateBetween(userInfo, start, end);
            } else {
                response = orderService.findOwnOrders(userInfo);
            }
        } else {
            if (isWeddingOrganizerId && isStatus) {
                response = orderService.findOrdersByWeddingOrganizerIdAndStatus(weddingOrganizerId, status);
            } else if (isWeddingOrganizerId) {
                response = orderService.findOrdersByWeddingOrganizerId(weddingOrganizerId);
            } else if (isStatus) {
                response = orderService.findOrdersByStatus(status);
            } else if (isWeddingPackageId) {
                response = orderService.findOrdersByWeddingPackageId(weddingPackageId);
            } else if (isStart && isEnd) {
                response = orderService.findOrdersByTransactionDateBetween(start, end);
            } else {
                response = orderService.findAllOrders();
            }
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_ACCEPT)
    public ResponseEntity<?> acceptOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.acceptOrder(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_REJECT)
    public ResponseEntity<?> rejectOrder(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.rejectOrder(userInfo, id);
        return ResponseEntity.ok(response);
    }



}
