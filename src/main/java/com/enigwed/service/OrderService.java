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
    List<Order> loadFinishedOrderByWeddingOrganizerIdAndTransactionDateBetween(String weddingOrganizerId, LocalDateTime from, LocalDateTime to);
    // Customer
    ApiResponse<OrderResponse> customerCreateOrder(OrderRequest orderRequest);
    ApiResponse<OrderResponse> customerFindOrderByBookCode(String bookCode);
    ApiResponse<OrderResponse> customerPayOrder(MultipartFile image, String orderId);
    ApiResponse<OrderResponse> customerCancelOrder(String orderId);
    ApiResponse<OrderResponse> customerReviewOrder(String orderId, ReviewRequest reviewRequest);
    // Wedding Organizer
    ApiResponse<List<OrderResponse>> findOwnOrders(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest, String keyword);
    ApiResponse<OrderResponse> findOwnOrderById(JwtClaim userInfo, String id);
    ApiResponse<OrderResponse> acceptOrder(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> rejectOrder(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> confirmPayment(JwtClaim userInfo, String orderId);
    ApiResponse<OrderResponse> finishOrder(JwtClaim userInfo, String orderId);
    // Admin
    ApiResponse<List<OrderResponse>> findAllOrders(FilterRequest filter, PagingRequest pagingRequest, String keyword);
    ApiResponse<OrderResponse> findOrderById(String id);
}
