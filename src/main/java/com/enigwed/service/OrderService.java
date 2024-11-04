package com.enigwed.service;

import com.enigwed.constant.EStatus;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.OrderRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.OrderResponse;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    // Customer
    ApiResponse<OrderResponse> createOrder(OrderRequest orderRequest);
    ApiResponse<OrderResponse> findOrderByBookCode(String bookCode);
    ApiResponse<OrderResponse> payOrder(MultipartFile image, String orderId);
    ApiResponse<OrderResponse> cancelOrder(String orderId);
    ApiResponse<OrderResponse> finishOrder(String orderId); // Add review later
    // Admin
    ApiResponse<OrderResponse> findOrderById(String id);
    ApiResponse<OrderResponse> confirmPayment(String orderId);
    ApiResponse<List<OrderResponse>> findAllOrders();
    ApiResponse<List<OrderResponse>> findOrdersByWeddingOrganizerId(String weddingOrganizerId);
    ApiResponse<List<OrderResponse>> findOrdersByWeddingPackageId(String weddingPackageId);
    ApiResponse<List<OrderResponse>> findOrdersByStatus(EStatus status);
    ApiResponse<List<OrderResponse>> findOrdersByWeddingOrganizerIdAndStatus(String weddingOrganizerId, EStatus status);
    ApiResponse<List<OrderResponse>> findOrdersByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    // Wedding Organizer
    ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo);
    ApiResponse<List<OrderResponse>> findOwnOrdersByStatus(JwtClaim userInfo, EStatus status);
    ApiResponse<List<OrderResponse>> findOwnOrdersByWeddingPackageId(JwtClaim userInfo, String weddingPackageId);
    ApiResponse<List<OrderResponse>> findOwnOrdersByTransactionDateBetween(JwtClaim userInfo, LocalDateTime start, LocalDateTime end);
    ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> rejectOrder(JwtClaim userInfo, String orderId);
}
