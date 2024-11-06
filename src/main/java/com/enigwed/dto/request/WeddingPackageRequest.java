package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.enigwed.constant.Constraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeddingPackageRequest {

    private String id;

    @NotBlank(message = NAME_BLANK)
    private String name;

    @Size(max = 10000, message = DESCRIPTION_MAX_10000)
    private String description;

    @Positive(message = PRICE_POSITIVE)
    private double price;

    @NotNull
    private ProvinceRequest province;

    @NotNull
    private RegencyRequest regency;

    private List<BonusDetailRequest> bonusDetails;
}
