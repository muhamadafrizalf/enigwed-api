package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Builder
public class AdditionalProduct {

    @NotBlank(message = PRODUCT_ID_BLANK)
    private String productId;

    @Positive(message = QUANTITY_INVALID)
    @NotNull(message = QUANTITY_NULL)
    private Integer quantity;
}
