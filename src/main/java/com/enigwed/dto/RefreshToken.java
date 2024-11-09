package com.enigwed.dto;

import com.enigwed.constant.SConstraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefreshToken {

    @NotBlank(message = SConstraint.TOKEN_IS_BLANK)
    private String token;
}
