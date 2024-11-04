package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.BonusPackageResponse;
import com.enigwed.entity.BonusPackage;
import com.enigwed.entity.Image;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.BonusPackageRepository;
import com.enigwed.service.BonusPackageService;
import com.enigwed.service.ImageService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BonusPackageServiceImpl implements BonusPackageService {
    private final BonusPackageRepository bonusPackageRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;

    private BonusPackage findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return bonusPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.BONUS_PACKAGE_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, BonusPackage bonusPackage) throws AccessDeniedException {
        String userCredentialId = bonusPackage.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Transactional(readOnly = true)
    @Override
    public BonusPackage loadBonusPackageById(String id) {
        try {
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error during loading bonus package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<BonusPackageResponse> findBonusPackageById(String id) {
        try {
            // ErrorResponse
            BonusPackage bonusPackage = findByIdOrThrow(id);
            BonusPackageResponse response = BonusPackageResponse.from(bonusPackage);
            return ApiResponse.success(response, Message.BONUS_PACKAGE_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading bonus package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BonusPackageResponse>> findAllBonusPackages() {
        List<BonusPackage> bonusPackageList = bonusPackageRepository.findByDeletedAtIsNull();
        if (bonusPackageList == null || bonusPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_BONUS_PACKAGE_FOUND);
        List<BonusPackageResponse> responses = bonusPackageList.stream().map(BonusPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.BONUS_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BonusPackageResponse>> findAllBonusPackagesByWeddingOrganizerId(String weddingOrganizerId) {
        List<BonusPackage> bonusPackageList = bonusPackageRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);
        if (bonusPackageList == null || bonusPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_BONUS_PACKAGE_FOUND);
        List<BonusPackageResponse> responses = bonusPackageList.stream().map(BonusPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.BONUS_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BonusPackageResponse>> searchBonusPackage(String keyword) {
        List<BonusPackage> bonusPackageList = bonusPackageRepository.searchBonusPackage(keyword);
        if (bonusPackageList == null || bonusPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_BONUS_PACKAGE_FOUND);
        List<BonusPackageResponse> responses = bonusPackageList.stream().map(BonusPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.BONUS_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BonusPackageResponse>> searchBonusPackageFromWeddingOrganizerId(String weddingOrganizerId, String keyword) {
        List<BonusPackage> bonusPackageList = bonusPackageRepository.findByWeddingOrganizerIdAndKeyword(weddingOrganizerId, keyword);
        if (bonusPackageList == null || bonusPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_BONUS_PACKAGE_FOUND);
        List<BonusPackageResponse> responses = bonusPackageList.stream().map(BonusPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.BONUS_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<BonusPackageResponse>> getOwnWeddingPackages(JwtClaim userInfo) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
        List<BonusPackage> bonusPackageList = bonusPackageRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());
        if (bonusPackageList == null || bonusPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_BONUS_PACKAGE_FOUND);
        List<BonusPackageResponse> responses = bonusPackageList.stream().map(BonusPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.BONUS_PACKAGES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BonusPackageResponse> createBonusPackage(JwtClaim userInfo, BonusPackageRequest bonusPackageRequest) {
        try {
            // ErrorResponse
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            // ValidationException
            validationUtil.validateAndThrow(bonusPackageRequest);
            // ErrorResponse
            if (bonusPackageRequest.getMaxQuantity()<bonusPackageRequest.getMinQuantity()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_MIN_MAX_QUANTITY);
            BonusPackage bonusPackage = BonusPackage.builder()
                    .name(bonusPackageRequest.getName())
                    .description(bonusPackageRequest.getDescription())
                    .price(bonusPackageRequest.getPrice())
                    .minQuantity(bonusPackageRequest.getMinQuantity())
                    .maxQuantity(bonusPackageRequest.getMaxQuantity())
                    .weddingOrganizer(weddingOrganizer)
                    .build();
            bonusPackage = bonusPackageRepository.save(bonusPackage);
            BonusPackageResponse response = BonusPackageResponse.from(bonusPackage);
            return ApiResponse.success(response, Message.BONUS_PACKAGE_CREATED);
        } catch (ValidationException e) {
            log.error("Validation error creating bonus: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error during creating bonus package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BonusPackageResponse> updateBonusPackage(JwtClaim userInfo, BonusPackageRequest bonusPackageRequest) {
        try {
            // ErrorResponse
            BonusPackage bonusPackage = findByIdOrThrow(bonusPackageRequest.getId());
            // AccessDeniedException
            validateUserAccess(userInfo, bonusPackage);
            // ValidationException
            validationUtil.validateAndThrow(bonusPackageRequest);
            // ErrorResponse
            if (bonusPackageRequest.getMaxQuantity()<bonusPackageRequest.getMinQuantity()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_MIN_MAX_QUANTITY);
            bonusPackage.setName(bonusPackageRequest.getName());
            bonusPackage.setDescription(bonusPackageRequest.getDescription());
            bonusPackage.setPrice(bonusPackageRequest.getPrice());
            bonusPackage.setMinQuantity(bonusPackageRequest.getMinQuantity());
            bonusPackage.setMaxQuantity(bonusPackageRequest.getMaxQuantity());
            bonusPackage = bonusPackageRepository.save(bonusPackage);
            BonusPackageResponse response = BonusPackageResponse.from(bonusPackage);
            return ApiResponse.success(response, Message.BONUS_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
          log.error("Access denied during updating bonus package: {}", e.getMessage());
          throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error during updating bonus package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error during updating bonus package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteBonusPackage(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            BonusPackage bonusPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, bonusPackage);
            bonusPackage.setDeletedAt(LocalDateTime.now());
            bonusPackageRepository.save(bonusPackage);
            return ApiResponse.success(Message.BONUS_PACKAGE_DELETED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during deletion bonus package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting bonus package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BonusPackageResponse> addBonusPackageImage(JwtClaim userInfo, String id, MultipartFile image) {
        try {
            // ErrorResponse
            BonusPackage bonusPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, bonusPackage);
            // ErrorResponse
            Image addedImage = imageService.createImage(image);
            if (bonusPackage.getImages() == null) {
                bonusPackage.setImages(new ArrayList<>());
            }
            bonusPackage.getImages().add(addedImage);
            bonusPackage = bonusPackageRepository.save(bonusPackage);
            BonusPackageResponse response = BonusPackageResponse.from(bonusPackage);
            return ApiResponse.success(response, Message.BONUS_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during adding bonus package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during adding bonus package image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<BonusPackageResponse> deleteBonusPackageImage(JwtClaim userInfo, String id, String imageId) {
        try {
            // ErrorResponse
            BonusPackage bonusPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, bonusPackage);
            // ErrorResponse
            imageService.deleteImage(imageId);
            List<Image> images = bonusPackage.getImages();
            if (images != null) {
                images.removeIf(image -> image.getId().equals(imageId));
            }
            bonusPackage = bonusPackageRepository.save(bonusPackage);
            BonusPackageResponse response = BonusPackageResponse.from(bonusPackage);
            return ApiResponse.success(response, Message.BONUS_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during deleting bonus package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting bonus package image: {}", e.getError());
            throw e;
        }
    }
}
