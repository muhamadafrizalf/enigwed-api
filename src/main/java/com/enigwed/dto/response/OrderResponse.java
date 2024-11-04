package com.enigwed.dto.response;

import com.enigwed.entity.Order;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponse {
    private String id;
    private LocalDateTime transactionDate;
    private LocalDateTime updatedAt;
    private LocalDateTime transactionFinishDate;
    private String bookCode;
    private LocalDateTime weddingDate;
    private double weddingPackageBasePrice;
    private String status;
    private CustomerResponse customer;
    private ImageResponse paymentImage;
    private WeddingOrganizerResponse weddingOrganizer;
    private WeddingPackageResponse weddingPackage;
    private List<OrderDetailResponse> orderDetails = new ArrayList<>();

    public static OrderResponse from(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setTransactionDate(order.getTransactionDate());
        response.setUpdatedAt(order.getUpdatedAt());
        response.setTransactionFinishDate(order.getTransactionFinishDate());
        response.setBookCode(order.getBookCode());
        response.setWeddingDate(order.getWeddingDate());
        response.setWeddingPackageBasePrice(order.getWeddingPackageBasePrice());
        response.setStatus(order.getStatus().name());
        response.setCustomer(CustomerResponse.from(order.getCustomer()));
        if (order.getPaymentImage() != null) {
            response.setPaymentImage(ImageResponse.from(order.getPaymentImage()));
        }
        response.setWeddingOrganizer(WeddingOrganizerResponse.from(order.getWeddingOrganizer()));
        response.setWeddingPackage(WeddingPackageResponse.from(order.getWeddingPackage()));
        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
            response.setOrderDetails(order.getOrderDetails().stream().map(OrderDetailResponse::from).toList());
        }
        return response;
    }
}
