package com.enigwed.dto.response;

import com.enigwed.dto.request.PagingRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse <T> {
    private Boolean success;
    private String message;
    private String error;
    private PagingResponse paging;
    private Map<String, Integer> countOrderByStatus;
    private Map<String, Integer> countUserStatus;
    private Map<String, Integer> countSubscriptionPaymentStatus;
    private T data;

    public static ApiResponse<?> failed(String message, String errorMessage) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .error(errorMessage)
                .build();
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

    public static <T> ApiResponse<List<T>> success(List<T> data, PagingRequest pagingRequest, String message) {
        int page = pagingRequest.getPage() - 1;
        int size = pagingRequest.getSize();
        int maxPages = Math.max((int) Math.ceil((double) data.size() / size) - 1, 0);
        int start = Math.min(page * size, maxPages * size);

        if (page > maxPages) {
            page = maxPages;
        }
        int end = Math.min((start + size), data.size());

        List<T> pageData = data.subList(start, end);

        Pageable pageable = PageRequest.of(page, size);

        Page<T> pageImpl = new PageImpl<>(pageData, pageable, data.size());

        return ApiResponse.<List<T>>builder()
                .success(true)
                .message(message)
                .data(pageImpl.getContent())
                .paging(PagingResponse.from(pageImpl))
                .build();
    }

    public static <T> ApiResponse<List<T>> successOrderList(List<T> data, PagingRequest pagingRequest, String message, Map<String, Integer> countByStatus) {
        ApiResponse<List<T>> response = success(data, pagingRequest, message);
        response.setCountOrderByStatus(countByStatus);
        return response;
    }

    public static <T> ApiResponse<List<T>> successWeddingOrganizerList(List<T> data, PagingRequest pagingRequest, String message, Map<String, Integer> countByStatus) {
        ApiResponse<List<T>> response = success(data, pagingRequest, message);
        response.setCountUserStatus(countByStatus);
        return response;
    }

    public static <T> ApiResponse<List<T>> successSubscriptionList(List<T> data, PagingRequest pagingRequest, String message, Map<String, Integer> countByStatus) {
        ApiResponse<List<T>> response = success(data, pagingRequest, message);
        response.setCountSubscriptionPaymentStatus(countByStatus);
        return response;
    }
}
