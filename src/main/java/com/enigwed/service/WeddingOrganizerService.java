package com.enigwed.service;

import com.enigwed.dto.response.ApiResponse;
import com.enigwed.entity.WeddingOrganizer;

import java.util.List;

public interface WeddingOrganizerService {
    void createWeddingOrganizer(WeddingOrganizer weddingOrganizer);

    ApiResponse<WeddingOrganizer> update (WeddingOrganizer weddingOrganizer);
    ApiResponse<WeddingOrganizer> findById(Long id);
    ApiResponse<List<WeddingOrganizer>> findAll();
    ApiResponse<?> deleteById(String id);
    ApiResponse<List<WeddingOrganizer>> search(String keyword);

}
