package com.enigwed.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.PAGE_INVALID;
import static com.enigwed.constant.SConstraint.SIZE_INVALID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingRequest {

    @Positive(message = PAGE_INVALID)
    @NotNull(message = PAGE_INVALID)
    private Integer page;

    @Positive(message = SIZE_INVALID)
    @NotNull(message = SIZE_INVALID)
    private Integer size;
}
