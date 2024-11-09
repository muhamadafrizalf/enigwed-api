package com.enigwed.dto.request;

import com.enigwed.constant.SConstraint;
import com.enigwed.constant.SMessage;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingRequest {

    @Positive(message = SConstraint.PAGE_INVALID)
    private Integer page;

    @Positive(message = SConstraint.SIZE_INVALID)
    private Integer size;
}
