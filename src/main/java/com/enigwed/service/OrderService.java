package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.OrderRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.ReviewRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.OrderResponse;
import com.enigwed.entity.Order;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderService {
    // Use In Other Service
    Order loadOrderById(String id);
    List<Order> loadAllOrders(String weddingOrganizerId, LocalDateTime from, LocalDateTime to);
    // Customer
    ApiResponse<OrderResponse> createOrder(OrderRequest orderRequest);
    ApiResponse<OrderResponse> findOrderByBookCode(String bookCode);
    ApiResponse<OrderResponse> payOrder(MultipartFile image, String orderId);
    ApiResponse<OrderResponse> cancelOrder(String orderId);
    ApiResponse<OrderResponse> reviewOrder(String orderId, ReviewRequest reviewRequest);
    // Admin
    ApiResponse<OrderResponse> findOrderById(String id);
    ApiResponse<List<OrderResponse>> findAllOrders(FilterRequest filter, PagingRequest pagingRequest);
    // Wedding Organizer
    ApiResponse<OrderResponse> findOwnOrderById(JwtClaim userInfo, String id);
    ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> rejectOrder(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> confirmPayment(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> finishOrder(JwtClaim userInfo, String orderId);
}
