package com.enigwed.dto.response;

import com.enigwed.entity.BonusDetail;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BonusDetailResponse {
    private String id;
    private int quantity;
    private ProductResponse bonusPackage;

    public static BonusDetailResponse from(BonusDetail bonusDetail) {
        BonusDetailResponse response = new BonusDetailResponse();
        response.setId(bonusDetail.getId());
        response.setQuantity(bonusDetail.getQuantity());
        response.setBonusPackage(ProductResponse.card(bonusDetail.getProduct()));
        return response;
    }
}
