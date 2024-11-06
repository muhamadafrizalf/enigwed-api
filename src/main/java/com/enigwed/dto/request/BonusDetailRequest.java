package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusDetailRequest {

    @NotBlank(message = PRODUCT_ID_BLANK)
    private String productId;

    @Positive(message = QUANTITY_POSITIVE)
    @NotNull(message = QUANTITY_NULL)
    private Integer quantity;
}
