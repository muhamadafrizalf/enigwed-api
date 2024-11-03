package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.WeddingOrganizer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingOrganizerService {
    void createWeddingOrganizer(WeddingOrganizer weddingOrganizer);

    WeddingOrganizer loadWeddingOrganizerById(String id);
    WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId);

    ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id);
    ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers();
    ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword);

    ApiResponse<WeddingOrganizerResponse> getOwnWeddingOrganizer(JwtClaim userInfo);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer (JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest);
    ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar);
    ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id);
    ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id);
}
