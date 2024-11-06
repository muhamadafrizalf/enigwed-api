package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.Constraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DistrictRequest {

    @NotBlank(message = DISTRICT_ID_BLANK)
    private String id;

    @NotBlank(message = REGENCY_ID_BLANK)
    private String regency_id;

    @NotBlank(message = DISTRICT_NAME_BLANK)
    private String name;
}
