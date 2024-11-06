package com.enigwed.mapper;

import com.enigwed.dto.request.PagingRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class PagingMapper {

    public static Pageable getPageable(PagingRequest request) {
        return PageRequest.of(request.getPage()-1, request.getSize());
    }

    public static <T> Page<T> listToPage(List<T> data, Pageable pageable) {
        return new PageImpl<>(data, pageable, data.size());
    }
}
