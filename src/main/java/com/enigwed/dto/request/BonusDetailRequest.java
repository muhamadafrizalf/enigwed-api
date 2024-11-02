package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.BONUS_PACKAGE_ID_BLANK;
import static com.enigwed.constant.Constraint.QUANTITY_POSITIVE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusDetailRequest {

    @NotBlank(message = BONUS_PACKAGE_ID_BLANK)
    private String bonusPackageId;

    @Positive(message = QUANTITY_POSITIVE)
    private Integer quantity;

    private boolean adjustable;
}
