package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductRequest {

    private String id;

    @NotBlank(message = PRODUCT_NAME_BLANK)
    private String name;

    @Size(max = 1000, message = DESCRIPTION_MAX_1000)
    @NotBlank(message = PRODUCT_DESCRIPTION_BLANK)
    private String description;

    @Positive(message = PRICE_INVALID)
    @NotNull(message = PRICE_NULL)
    private Double price;
}
