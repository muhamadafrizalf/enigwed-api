package com.enigwed.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BonusPackageRequest {

    private String id;

    @NotBlank(message = NAME_BLANK)
    private String name;

    @Size(max = 1000, message = DESCRIPTION_MAX)
    private String description;

    @Positive(message = PRICE_POSITIVE)
    private double price;

    @Positive(message = QUANTITY_POSITIVE)
    private int minQuantity;

    @Positive(message = QUANTITY_POSITIVE)
    private int maxQuantity;
}
