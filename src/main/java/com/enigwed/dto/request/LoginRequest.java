package com.enigwed.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;
import static com.enigwed.constant.SConstraint.PASSWORD_MIN_6;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {

    @Email(message = EMAIL_INVALID)
    @NotBlank(message = EMAIL_BLANK)
    private String email;

    @Size(min = 6, message = PASSWORD_MIN_6)
    @NotBlank(message = PASSWORD_BLANK)
    private String password;
}
