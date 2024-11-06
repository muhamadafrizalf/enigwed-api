package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class WeddingOrganizerRequest {

    private String id;

    @NotBlank(message = WEDDING_ORGANIZER_NAME_BLANK)
    private String name;

    @NotBlank(message = PHONE_BLANK)
    private String phone;

    @Size(max = 1000, message = DESCRIPTION_MAX)
    private String description;

    @NotBlank(message = ADDRESS_BLANK)
    private String address;

    @NotNull
    private ProvinceRequest province;

    @NotNull
    private RegencyRequest regency;

    @NotNull
    private DistrictRequest district;
}
