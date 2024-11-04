package com.enigwed.dto.response;

import com.enigwed.entity.OrderDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderDetailResponse {
    BonusPackageResponse bonusPackage;
    private double price;
    private int quantity;

    public static OrderDetailResponse from(OrderDetail orderDetail) {
        OrderDetailResponse response = new OrderDetailResponse();
        response.setBonusPackage(BonusPackageResponse.from(orderDetail.getBonusPackage()));
        response.setPrice(orderDetail.getPrice());
        response.setQuantity(orderDetail.getQuantity());
        return response;
    }
}
