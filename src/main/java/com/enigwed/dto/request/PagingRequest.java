package com.enigwed.dto.request;

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

    @Positive(message = "")
    private Integer page;

    private Integer size;

    public static PagingRequest of(Integer page, Integer size) {
        return PagingRequest.builder().page(page).size(size).build();
    }
}
