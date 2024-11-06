package com.enigwed.dto.response;

import com.enigwed.entity.OrderDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderDetailResponse {
    ProductResponse bonusPackage;
    private double price;
    private int quantity;
    private boolean bonus;

    public static OrderDetailResponse simple(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setBonusPackage(ProductResponse.simple(orderDetail.getProduct()));
        response.setPrice(orderDetail.getPrice());
        response.setQuantity(orderDetail.getQuantity());
        response.setBonus(orderDetail.isBonus());
        return response;
    }
}
