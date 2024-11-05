package com.enigwed.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.*;
import static com.enigwed.constant.Constraint.EMAIL_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerRequest {

    @NotBlank(message = NAME_BLANK)
    private String name;

    @NotBlank(message = PHONE_BLANK)
    private String email;

    @Email(message = EMAIL_VALID)
    @NotBlank(message = EMAIL_BLANK)
    private String phone;

    @Size(max = 1000, message = ADDRESS_MAX)
    private String address;
}
