package com.enigwed.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = WEDDING_ORGANIZER_NAME_BLANK)
    private String name;

    @Size(max = 1000, message = DESCRIPTION_MAX_1000)
    @NotBlank(message = WEDDING_ORGANIZER_DESCRIPTION_BLANK)
    private String description;

    @Size(max = 1000, message = ADDRESS_MAX_1000)
    @NotBlank(message = ADDRESS_BLANK)
    private String address;

    @NotBlank(message = NPWP_BLANK)
    private String npwp;

    @Pattern(regexp = "^[0-9]+$", message = NIB_INVALID)
    @NotBlank(message = NIB_BLANK)
    private String nib;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$|^0\\d{9,15}$|^\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{4}$", message = PHONE_INVALID)
    @NotBlank(message = PHONE_BLANK)
    private String phone;

    @Email(message = EMAIL_INVALID)
    @NotBlank(message = EMAIL_BLANK)
    private String email;

    @Size(min = 6, message = PASSWORD_MIN_6)
    @NotBlank(message = PASSWORD_BLANK)
    private String password;

    @NotBlank(message = CONFIRM_PASSWORD_BLANK)
    private String confirmPassword;

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
