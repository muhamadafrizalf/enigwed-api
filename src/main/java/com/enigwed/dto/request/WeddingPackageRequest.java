package com.enigwed.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeddingPackageRequest {

    private String id;

    @NotBlank(message = WEDDING_PACKAGE_NAME_BLANK)
    private String name;

    @Size(max = 10000, message = DESCRIPTION_MAX_10000)
    @NotBlank(message = WEDDING_PACKAGE_DESCRIPTION_BLANK)
    private String description;

    @Positive(message = PRICE_INVALID)
    @NotNull(message = PRICE_NULL)
    private Double price;

    @NotNull(message = PROVINCE_NULL)
    private ProvinceRequest province;

    @NotNull(message = REGENCY_NULL)
    private RegencyRequest regency;

    @Valid
    private List<BonusDetailRequest> bonusDetails;
}
