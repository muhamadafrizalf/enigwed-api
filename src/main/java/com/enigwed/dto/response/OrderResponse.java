package com.enigwed.dto.response;

import com.enigwed.constant.EStatus;
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
    private CustomerResponse customer;
    private ImageResponse paymentImage;
    private WeddingOrganizerResponse weddingOrganizer;
    private WeddingPackageResponse weddingPackage;
    private List<OrderDetailResponse> orderDetails = new ArrayList<>();
    private Boolean reviewed;
    private ReviewResponse review;

    public static OrderResponse simple(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBookCode(order.getBookCode());
        response.setTransactionDate(order.getTransactionDate());
        response.setWeddingDate(order.getWeddingDate());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
        response.setCustomer(CustomerResponse.all(order.getCustomer()));
        response.setWeddingPackage(WeddingPackageResponse.order(order.getWeddingPackage()));
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(order.getWeddingOrganizer()));
        response.setReviewed(order.isReviewed());
        return response;
    }

    public static OrderResponse all(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTransactionDate(order.getTransactionDate());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setTransactionFinishDate(order.getTransactionFinishDate());
        response.setBookCode(order.getBookCode());
        response.setWeddingDate(order.getWeddingDate());
        response.setBasePrice(order.getBasePrice());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
        response.setCustomer(CustomerResponse.all(order.getCustomer()));
        if (order.getPaymentImage() != null) {
            response.setPaymentImage(ImageResponse.from(order.getPaymentImage()));
        }
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(order.getWeddingOrganizer()));
        response.setWeddingPackage(WeddingPackageResponse.order(order.getWeddingPackage()));
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            response.setOrderDetails(order.getOrderDetails().stream().map(OrderDetailResponse::simple).toList());
        }
        response.setReviewed(order.isReviewed());
        if (order.getReview() != null) {
            response.setReview(ReviewResponse.simple(order.getReview()));
        }
        return response;
    }

    public static OrderResponse information(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setBookCode(order.getBookCode());
        response.setCustomer(CustomerResponse.all(order.getCustomer()));
        response.setTransactionDate(order.getTransactionDate());
        response.setTransactionFinishDate(order.getTransactionFinishDate());
        response.setWeddingDate(order.getWeddingDate());
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(order.getWeddingOrganizer()));
        response.setWeddingPackage(WeddingPackageResponse.order(order.getWeddingPackage()));
        response.setBasePrice(order.getBasePrice());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
        if (order.getPaymentImage() != null) {
            response.setPaymentImage(ImageResponse.from(order.getPaymentImage()));
        }
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            response.setOrderDetails(order.getOrderDetails().stream().map(OrderDetailResponse::simple).toList());
        }
        if (order.getStatus() == EStatus.FINISHED && !order.isReviewed()) {
            response.setReviewed(false);
        } else if (order.getStatus() == EStatus.FINISHED) {
            response.setReviewed(true);
            response.setReview(ReviewResponse.simple(order.getReview()));
        }
        return response;
    }


}
