package com.enigwed.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class RegisterRequest {

    @NotBlank(message = NAME_BLANK)
    private String name;

    @Size(max = 1000, message = DESCRIPTION_MAX)
    private String description;

    @Size(max = 1000, message = ADDRESS_MAX)
    @NotBlank(message = ADDRESS_BLANK)
    private String address;

    @NotBlank(message = NPWP_BLANK)
    private String npwp;

    @NotBlank(message = NIB_BLANK)
    private String nib;

    @NotBlank(message = CITY_ID_BLANK)
    private String cityId;

    @NotBlank(message = PHONE_BLANK)
    private String phone;

    @Email(message = EMAIL_VALID)
    @NotBlank(message = EMAIL_BLANK)
    private String email;

    @NotBlank(message = PASSWORD_BLANK)
    @Size(min = 6, message = PASSWORD_MIN)
    private String password;

    @NotBlank(message = CONFIRM_PASSWORD_BLANK)
    private String confirmPassword;
}
