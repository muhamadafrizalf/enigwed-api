package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.WeddingOrganizer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingOrganizerService {
    // Register
    void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) throws DataIntegrityViolationException;

    // Use in other service
    WeddingOrganizer loadWeddingOrganizerById(String id);
    WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId);

    // Customer
    ApiResponse<WeddingOrganizerResponse> customerFindWeddingOrganizerById(String id);
    ApiResponse<List<WeddingOrganizerResponse>> customerFindAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<List<WeddingOrganizerResponse>> customerSearchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest);

    // WO
    ApiResponse<WeddingOrganizerResponse> getOwnWeddingOrganizer(JwtClaim userInfo);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer (JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar);
    ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id);
    ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id);

    // ADMIN
    ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id);
    ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id);
    ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest);
}
