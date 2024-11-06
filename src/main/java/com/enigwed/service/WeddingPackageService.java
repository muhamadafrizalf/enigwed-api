package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingPackageResponse;
import com.enigwed.entity.WeddingPackage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingPackageService {
    // Use in other service
    WeddingPackage loadWeddingPackageById(String id);

    // Customer
    ApiResponse<WeddingPackageResponse> customerFindWeddingPackageById(String id);
    ApiResponse<List<WeddingPackageResponse>> customerFindAllWeddingPackages(FilterRequest filter);
    ApiResponse<List<WeddingPackageResponse>> customerSearchWeddingPackage(String keyword, FilterRequest filter);

    // WO
    ApiResponse<WeddingPackageResponse> getOwnWeddingPackageById(JwtClaim userInfo, String id);
    ApiResponse<List<WeddingPackageResponse>> getOwnWeddingPackages(JwtClaim userInfo,FilterRequest filter);
    ApiResponse<List<WeddingPackageResponse>> searchOwnWeddingPackages(JwtClaim userInfo, String keyword, FilterRequest filter);
    ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo,WeddingPackageRequest weddingPackageRequest);
    ApiResponse<WeddingPackageResponse> updateWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest);
    ApiResponse<?> deleteWeddingPackage(JwtClaim userInfo, String id);
    ApiResponse<WeddingPackageResponse> addWeddingPackageImage(JwtClaim userInfo, String id, MultipartFile image);
    ApiResponse<WeddingPackageResponse> deleteWeddingPackageImage(JwtClaim userInfo, String id, String imageId);

    // ADMIN
    ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id);
    ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages(FilterRequest filter);
    ApiResponse<List<WeddingPackageResponse>>searchWeddingPackage(String keyword, FilterRequest filter);
}
