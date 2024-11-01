package com.enigwed.service;

import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.ImageResponse;
import com.enigwed.entity.Image;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageService {
    Image create(MultipartFile file);
    Resource findById(String id);
    Image update(String id, MultipartFile image);
    void deleteById(String id);

    ApiResponse<ImageResponse> findByIdResponse(String id);
    ApiResponse<ImageResponse> updateResponse(String id, MultipartFile image);
    ApiResponse<?> softDeleteById(String id);

}
