package com.enigwed.dto.response;

import com.enigwed.entity.BonusDetail;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BonusDetailResponse {
    private String id;
    private int quantity;
    private boolean adjustable;
    private BonusPackageResponse bonusPackage;

    public static BonusDetailResponse from(BonusDetail bonusDetail) {
        BonusDetailResponse response = new BonusDetailResponse();
        response.setId(bonusDetail.getId());
        response.setQuantity(bonusDetail.getQuantity());
        response.setAdjustable(bonusDetail.isAdjustable());
        response.setBonusPackage(BonusPackageResponse.from(bonusDetail.getBonusPackage()));
        return response;
    }
}
