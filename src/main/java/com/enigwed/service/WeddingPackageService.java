package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingPackageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingPackageService {

    ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id);
    ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages();
    ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackagesByWeddingOrganizerId(String weddingOrganizerId);
    ApiResponse<List<WeddingPackageResponse>> searchWeddingPackage(String keyword);
    ApiResponse<List<WeddingPackageResponse>> searchWeddingPackageFromWeddingOrganizerId(String weddingOrganizerId, String keyword);

    ApiResponse<List<WeddingPackageResponse>> getOwnWeddingPackages(JwtClaim userInfo);
    ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo,WeddingPackageRequest weddingPackageRequest);
    ApiResponse<WeddingPackageResponse> updateWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest);
    ApiResponse<?> deleteWeddingPackage(JwtClaim userInfo, String id);
    ApiResponse<WeddingPackageResponse> addWeddingPackageImage(JwtClaim userInfo, String id, MultipartFile image);
    ApiResponse<WeddingPackageResponse> deleteWeddingPackageImage(JwtClaim userInfo, String id, String imageId);

}
