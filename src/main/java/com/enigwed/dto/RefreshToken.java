package com.enigwed.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.TOKEN_IS_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @NotBlank(message = TOKEN_IS_BLANK)
    private String token;
}
