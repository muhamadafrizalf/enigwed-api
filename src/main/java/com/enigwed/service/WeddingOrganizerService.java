package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.WeddingOrganizer;

import java.util.List;

public interface WeddingOrganizerService {
    void createWeddingOrganizer(WeddingOrganizer weddingOrganizer);
    ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id);
    ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizer();
    ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer (JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest);
    ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id);

}
