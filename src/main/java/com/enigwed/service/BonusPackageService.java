package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BonusPackageResponse;
import com.enigwed.entity.BonusPackage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BonusPackageService {
    BonusPackage loadBonusPackageById(String id);
    ApiResponse<BonusPackageResponse> createBonusPackage(JwtClaim userInfo, BonusPackageRequest bonusPackageRequest);
    ApiResponse<BonusPackageResponse> findBonusPackageById(String id);
    ApiResponse<List<BonusPackageResponse>> findAllBonusPackages();
    ApiResponse<List<BonusPackageResponse>> findAllBonusPackagesByWeddingOrganizerId(String weddingOrganizerId);
    ApiResponse<List<BonusPackageResponse>> searchBonusPackage(String keyword);
    ApiResponse<List<BonusPackageResponse>> searchBonusPackageFromWeddingOrganizerId(String weddingOrganizerId, String keyword);
    ApiResponse<BonusPackageResponse> updateBonusPackage(JwtClaim userInfo, BonusPackageRequest bonusPackageRequest);
    ApiResponse<?> deleteBonusPackage(JwtClaim userInfo, String id);
    ApiResponse<BonusPackageResponse> addBonusPackageImage (JwtClaim userInfo, String id, MultipartFile image);
    ApiResponse<BonusPackageResponse> deleteBonusPackageImage(JwtClaim userInfo, String id, String imageId);
}
