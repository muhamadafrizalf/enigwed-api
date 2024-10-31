package com.enigwed.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.*;
import static com.enigwed.constant.Constraint.PASSWORD_MIN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = EMAIL_VALID)
    @NotBlank(message = EMAIL_BLANK)
    private String email;

    @NotBlank(message = PASSWORD_BLANK)
    @Size(min = 6, message = PASSWORD_MIN)
    private String password;
}
