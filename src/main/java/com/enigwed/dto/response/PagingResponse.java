package com.enigwed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagingResponse {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    public static <T> PagingResponse from(Page<T> data) {
        PagingResponse response = new PagingResponse();
        response.setPage(data.getNumber()+1);
        response.setSize(data.getSize());
        response.setTotalElements(data.getTotalElements());
        response.setTotalPages(Math.max(data.getTotalPages(), 1));
        return response;
    }
}
