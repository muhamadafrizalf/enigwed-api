package com.enigwed.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;
import static com.enigwed.constant.SConstraint.EMAIL_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = CUSTOMER_NAME_BLANK)
    private String name;

    @NotBlank(message = PHONE_BLANK)
    private String email;

    @Email(message = EMAIL_INVALID)
    @NotBlank(message = EMAIL_BLANK)
    private String phone;

    @Size(max = 1000, message = ADDRESS_MAX_1000)
    @NotBlank(message = ADDRESS_BLANK)
    private String address;
}
