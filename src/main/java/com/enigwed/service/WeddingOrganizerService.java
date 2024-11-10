package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.SubscriptionPackage;
import com.enigwed.entity.WeddingOrganizer;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingOrganizerService {
    // Use in other service
    void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) throws DataIntegrityViolationException;
    WeddingOrganizer loadWeddingOrganizerById(String id);
    WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId);
    WeddingOrganizer loadWeddingOrganizerByEmail(String email);
    void extendWeddingOrganizerSubscription(WeddingOrganizer weddingOrganizer, SubscriptionPackage subscriptionPackage);
    List<WeddingOrganizer> findAllWeddingOrganizers();

    // Customer
    ApiResponse<List<WeddingOrganizerResponse>> customerFindAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<List<WeddingOrganizerResponse>> customerSearchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<WeddingOrganizerResponse> customerFindWeddingOrganizerById(String id);

    // WO
    ApiResponse<WeddingOrganizerResponse> findOwnWeddingOrganizer(JwtClaim userInfo);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer (JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest);
    ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id);
    ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar);
    ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id);

    // ADMIN
    ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest);
    ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id);
    ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id);
    ApiResponse<WeddingOrganizerResponse> deactivateWeddingOrganizer(String id);
}
