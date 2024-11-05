package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import static com.enigwed.constant.Constraint.BONUS_PACKAGE_ID_BLANK;
import static com.enigwed.constant.Constraint.QUANTITY_POSITIVE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class OrderDetailRequest {

    @NotBlank(message = BONUS_PACKAGE_ID_BLANK)
    private String bonusPackageId;

    @Positive(message = QUANTITY_POSITIVE)
    private int quantity;
}
