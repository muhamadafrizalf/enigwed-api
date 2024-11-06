package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import static com.enigwed.constant.Constraint.PRODUCT_ID_BLANK;
import static com.enigwed.constant.Constraint.QUANTITY_POSITIVE;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class AdditionalProduct {

    @NotBlank(message = PRODUCT_ID_BLANK)
    private String bonusPackageId;

    @Positive(message = QUANTITY_POSITIVE)
    private int quantity;
}
