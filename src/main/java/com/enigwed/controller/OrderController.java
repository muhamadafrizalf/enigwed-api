package com.enigwed.controller;

import com.enigwed.constant.ERole;
import com.enigwed.constant.EStatus;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.OrderRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.ReviewRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.OrderService;
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
public class OrderController {
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    // Customer
    @Operation(
            summary = "To create order by customer (MOBILE)",
            description = "Create order, send notification to wedding organizer"
    )
    @PostMapping(PathApi.PUBLIC_ORDER)
    public ResponseEntity<?> createOrder(
            @RequestBody OrderRequest orderRequest
    ) {
        ApiResponse<?> response = orderService.createOrder(orderRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To get order by book_code (MOBILE)")
    @GetMapping(PathApi.PUBLIC_ORDER)
    public ResponseEntity<?> getOrderByBookCode(
            @RequestParam String bookCode
    ) {
        ApiResponse<?> response = orderService.findOrderByBookCode(bookCode);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To upload order payment image, update order by order_id (MOBILE)",
            description = "Add payment image, update status to CHECKING_PAYMENT, send notification to WO"
    )
    @PutMapping(value = PathApi.PUBLIC_ORDER_ID_PAY, consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateOrder(
            @PathVariable String id,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        ApiResponse<?> response = orderService.payOrder(image, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To cancel order, update order by order_id (MOBILE)",
            description = "Update status to CANCELED, send notification to wedding organizer"
    )
    @PutMapping(PathApi.PUBLIC_ORDER_ID_CANCEL)
    public ResponseEntity<?> cancelOrder(
            @PathVariable String id
    ) {
        ApiResponse<?> response = orderService.cancelOrder(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To review order, update order by order_id (MOBILE)",
            description = "Update order reviewed true, post review here [SOON], send notification to wedding organizer"
    )
    @PutMapping(PathApi.PUBLIC_ORDER_ID_REVIEW)
    public ResponseEntity<?> reviewOrder(
            @PathVariable String id,
            @RequestBody ReviewRequest reviewRequest
    ) {
        ApiResponse<?> response = orderService.reviewOrder(id, reviewRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get order by order_id [ADMIN, WO] (WEB)",
            description = "Admin can get all order, WO can only get if it's their own order"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_ORDER_ID)
    public ResponseEntity<?> getOrderById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = orderService.findOwnOrderById(userInfo, id);
        } else {
            response = orderService.findOrderById(id);
        }

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "To get all orders [ADMIN, WO] (WEB)",
            description = "ADMIN can get all orders, WO can only get own orders"
    )
    @PreAuthorize("hasAnyRole('ADMIN', 'WO')")
    @GetMapping(PathApi.PROTECTED_ORDER)
    public ResponseEntity<?> getAllOrders(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @RequestParam(required = false) String weddingOrganizerId,
            @RequestParam(required = false) EStatus status,
            @RequestParam(required = false) String weddingPackageId,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        FilterRequest filter = new FilterRequest();
        if (weddingOrganizerId != null && !weddingOrganizerId.isEmpty()) filter.setWeddingOrganizerId(weddingOrganizerId);
        if (status != null) filter.setOrderStatus(status);
        if (weddingPackageId != null && !weddingPackageId.isEmpty()) filter.setWeddingPackageId(weddingPackageId);
        if (startDate != null) filter.setStartDate(startDate);
        if (endDate != null) filter.setEndDate(endDate);

        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response;
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            response = orderService.findOwnOrders(userInfo, filter, pagingRequest);
        } else {
            response = orderService.findAllOrders(filter, pagingRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For WO to confirm payment by order_id [WO] (WEB)",
            description = "Update status to PAID, send notification to wedding organizer"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_CONFIRM)
    public ResponseEntity<?> confirmPayment(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.confirmPayment(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to accept order by order_id [WO] (WEB)",
            description = "Update status to WAITING_FOR_PAYMENT, send notification to customer [SOON]"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_ACCEPT)
    public ResponseEntity<?> acceptOrder(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.acceptOrder(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to reject order by order_id [WO] (WEB)",
            description = "Update status to REJECTED, send notification to customer [SOON]"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_REJECT)
    public ResponseEntity<?> rejectOrder(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.rejectOrder(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to finish order by order_id [WO] (WEB)",
            description = "Update status to FINISHED, send notification to customer [SOON]"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(PathApi.PROTECTED_ORDER_ID_FINISH)
    public ResponseEntity<?> finishOrder(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = orderService.finishOrder(userInfo, id);
        return ResponseEntity.ok(response);
    }

}
