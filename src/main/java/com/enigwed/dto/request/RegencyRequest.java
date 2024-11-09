package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegencyRequest {

    @NotBlank(message = REGENCY_ID_BLANK)
    private String id;

    @NotBlank(message = PROVINCE_ID_BLANK)
    private String province_id;

    @NotBlank(message = REGENCY_NAME_BLANK)
    private String name;
}
