package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.PROVINCE_ID_BLANK;
import static com.enigwed.constant.SConstraint.PROVINCE_NAME_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProvinceRequest {

    @NotBlank(message = PROVINCE_ID_BLANK)
    private String id;

    @NotBlank(message = PROVINCE_NAME_BLANK)
    private String name;
}
