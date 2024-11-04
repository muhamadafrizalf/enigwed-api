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
    ApiResponse<OrderResponse> acceptPayment(String orderId);
    ApiResponse<OrderResponse> rejectPayment(String orderId);
    List<OrderResponse> findAllOrders();
    List<OrderResponse> findOrdersByWeddingOrganizerId(String weddingOrganizerId);
    List<OrderResponse> findOrdersByWeddingPackageId(String weddingPackageId);
    List<OrderResponse> findOrdersByStatus(EStatus status);
    List<OrderResponse> findOrdersByWeddingOrganizerIdAndStatus(String weddingOrganizerId, EStatus status);
    List<OrderResponse> findOrdersByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    // Wedding Organizer
    List<OrderResponse> findOwnOrders(JwtClaim userInfo);
    List<OrderResponse> findOwnOrdersByStatus(JwtClaim userInfo, EStatus status);
    OrderResponse acceptOrder(JwtClaim userInfo, String orderId);
    OrderResponse rejectOrder(JwtClaim userInfo, String orderId);
}
