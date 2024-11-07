package com.enigwed.dto.request;

import com.enigwed.constant.Message;
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

    @Positive(message = Message.PAGE_INVALID)
    private Integer page;

    @Positive(message = Message.SIZE_INVALID)
    private Integer size;

    public static PagingRequest of(Integer page, Integer size) {
        return PagingRequest.builder().page(page).size(size).build();
    }
}
