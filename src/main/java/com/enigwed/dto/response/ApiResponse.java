package com.enigwed.dto.response;

import com.enigwed.dto.request.PagingRequest;
import com.enigwed.mapper.PagingMapper;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    private boolean success;
    private String message;
    private PagingResponse paging;
    private T data;
    private String error;

    public static <T> ApiResponse<List<T>> success(Page<T> data, String message) {
        return ApiResponse.<List<T>>builder()
                .success(true)
                .message(message)
                .data(data.getContent())
                .paging(PagingResponse.from(data))
                .build();
    }

    public static <T> ApiResponse<List<T>> success(List<T> data, PagingRequest pagingRequest, String message) {
        Pageable pageable = PagingMapper.getPageable(pagingRequest);
        Page<T> page = PagingMapper.listToPage(data, pageable);
        return success(page, message);
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<?> success(String message) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    public static ApiResponse<?> failed(String message, String errorMessage) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .error(errorMessage)
                .build();
    }
}
