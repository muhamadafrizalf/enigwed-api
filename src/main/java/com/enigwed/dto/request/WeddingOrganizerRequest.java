package com.enigwed.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class WeddingOrganizerRequest {

    private String id;

    @NotBlank(message = WEDDING_ORGANIZER_NAME_BLANK)
    private String name;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$|^0\\d{9,15}$|^\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$", message = PHONE_INVALID)
    @NotBlank(message = PHONE_BLANK)
    private String phone;

    @Size(max = 1000, message = DESCRIPTION_MAX_1000)
    @NotBlank(message = WEDDING_ORGANIZER_DESCRIPTION_BLANK)
    private String description;

    @Size(max = 1000, message = ADDRESS_MAX_1000)
    @NotBlank(message = ADDRESS_BLANK)
    private String address;

    @Valid
    @NotNull(message = PROVINCE_NULL)
    private ProvinceRequest province;

    @Valid
    @NotNull(message = REGENCY_NULL)
    private RegencyRequest regency;

    @Valid
    @NotNull(message = DISTRICT_NULL)
    private DistrictRequest district;
}
