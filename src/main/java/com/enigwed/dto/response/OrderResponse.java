package com.enigwed.dto.response;

import com.enigwed.entity.Order;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderResponse {
    private String id;
    private String bookCode;
    private LocalDateTime transactionDate;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionFinishDate;
    private LocalDateTime weddingDate;
    private Double basePrice;
    private Double totalPrice;
    private String status;
    private Boolean reviewed;
    private ReviewResponse review;
    private CustomerResponse customer;
    private ImageResponse paymentImage;
    private WeddingOrganizerResponse weddingOrganizer;
    private WeddingPackageResponse weddingPackage;
    private List<OrderDetailResponse> orderDetails = new ArrayList<>();

    public static OrderResponse card(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBookCode(order.getBookCode());
        response.setTransactionDate(order.getTransactionDate());
        response.setWeddingDate(order.getWeddingDate());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
        response.setCustomer(CustomerResponse.from(order.getCustomer()));
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(order.getWeddingOrganizer()));
        response.setWeddingPackage(WeddingPackageResponse.card(order.getWeddingPackage()));
        response.setReviewed(order.isReviewed());
        return response;
    }

    public static OrderResponse information(Order order) {
        OrderResponse response = OrderResponse.card(order);
        response.setTransactionFinishDate(order.getTransactionFinishDate());
        response.setBasePrice(order.getBasePrice());
        if (order.getPaymentImage() != null) {
            response.setPaymentImage(ImageResponse.from(order.getPaymentImage()));
        }
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            response.setOrderDetails(order.getOrderDetails().stream().map(OrderDetailResponse::simple).toList());
        }
        if (order.getReview() != null) {
            response.setReview(ReviewResponse.from(order.getReview()));
        }
        return response;
    }

    public static OrderResponse all(Order order) {
        OrderResponse response = OrderResponse.information(order);
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }
}
