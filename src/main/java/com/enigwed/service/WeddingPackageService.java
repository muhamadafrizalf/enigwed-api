package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingPackageResponse;
import com.enigwed.entity.WeddingPackage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface WeddingPackageService {
    // Use in other service
    WeddingPackage loadWeddingPackageById(String id);
    void addOrderCount(WeddingPackage weddingPackage);
    // Customer
    ApiResponse<List<WeddingPackageResponse>> customerFindAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest, String keyword);
    ApiResponse<WeddingPackageResponse> customerFindWeddingPackageById(String id);
    // Wedding organizer
    ApiResponse<List<WeddingPackageResponse>> findOwnWeddingPackages(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest, String keyword);
    ApiResponse<WeddingPackageResponse> findOwnWeddingPackageById(JwtClaim userInfo, String id);
    ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo,WeddingPackageRequest weddingPackageRequest);
    ApiResponse<WeddingPackageResponse> updateWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest);
    ApiResponse<?> deleteWeddingPackage(JwtClaim userInfo, String id);
    ApiResponse<WeddingPackageResponse> addWeddingPackageImage(JwtClaim userInfo, String id, MultipartFile image);
    ApiResponse<WeddingPackageResponse> deleteWeddingPackageImage(JwtClaim userInfo, String id, String imageId);
    // Admin
    ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest, String keyword);
    ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id);
}
