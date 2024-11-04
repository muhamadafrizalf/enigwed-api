package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusDetailRequest;
import com.enigwed.dto.request.WeddingPackageRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingPackageResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.WeddingPackageRepository;
import com.enigwed.service.*;
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
public class WeddingPackageServiceImpl implements WeddingPackageService {
    private final WeddingPackageRepository weddingPackageRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final CityService cityService;
    private final BonusPackageService bonusPackageService;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;

    private WeddingPackage findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return weddingPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_PACKAGE_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, WeddingPackage weddingPackage) throws AccessDeniedException {
        String userCredentialId = weddingPackage.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingPackage loadWeddingPackageById(String id) {
        try {
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error during loading wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            WeddingPackageResponse response = WeddingPackageResponse.from(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages() {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByDeletedAtIsNull();
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackagesByWeddingOrganizerId(String weddingOrganizerId) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> searchWeddingPackage(String keyword) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.searchWeddingPackage(keyword);
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> searchWeddingPackageFromWeddingOrganizerId(String weddingOrganizerId, String keyword) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByWeddingOrganizerIdAndKeyword(weddingOrganizerId, keyword);
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.WEDDING_PACKAGES_FOUND);
    }
    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> getOwnWeddingPackages(JwtClaim userInfo) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::from).toList();
        return ApiResponse.success(responses, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest) {
        try {
            // ErrorResponse
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            // ErrorResponse
            City city = cityService.loadCityById(weddingPackageRequest.getCityId());
            // ValidationException
            validationUtil.validateAndThrow(weddingPackageRequest);
            WeddingPackage weddingPackage = WeddingPackage.builder()
                    .name(weddingPackageRequest.getName())
                    .description(weddingPackageRequest.getDescription())
                    .basePrice(weddingPackageRequest.getBasePrice())
                    .weddingOrganizer(weddingOrganizer)
                    .city(city)
                    .build();
            List<BonusDetail> bonusDetails = new ArrayList<>();
            if(weddingPackageRequest.getBonusDetails() != null && !weddingPackageRequest.getBonusDetails().isEmpty()) {
                for (BonusDetailRequest bonusDetailRequest: weddingPackageRequest.getBonusDetails()) {
                    // ErrorResponse
                    BonusPackage bonusPackage = bonusPackageService.loadBonusPackageById(bonusDetailRequest.getBonusPackageId());
                    int quantity;
                    // ErrorResponse
                    if (bonusDetailRequest.getQuantity() == null) {
                        quantity = bonusPackage.getMinQuantity();
                    } else if (bonusDetailRequest.getQuantity() < bonusPackage.getMinQuantity()) {
                        throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_QUANTITY_MIN);
                    } else if (bonusDetailRequest.getQuantity() > bonusPackage.getMaxQuantity()) {
                        throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_QUANTITY_MAX);
                    } else {
                        quantity = bonusDetailRequest.getQuantity();
                    }
                    BonusDetail bonusDetail = BonusDetail.builder()
                            .weddingPackage(weddingPackage)
                            .bonusPackage(bonusPackage)
                            .quantity(quantity)
                            .adjustable(bonusDetailRequest.isAdjustable())
                            .build();
                    bonusDetails.add(bonusDetail);
                }
            }
            weddingPackage.setBonusDetails(bonusDetails);
            weddingPackage = weddingPackageRepository.save(weddingPackage);
            WeddingPackageResponse response = WeddingPackageResponse.from(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_CREATED);
        } catch (ValidationException e) {
            log.error("Validation error creating wedding package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error during creating wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> updateWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(weddingPackageRequest.getId());
            // AccessDeniedException
            validateUserAccess(userInfo, weddingPackage);
            // ValidationException
            validationUtil.validateAndThrow(weddingPackageRequest);
            // ErrorResponse
            City city = cityService.loadCityById(weddingPackageRequest.getCityId());
            weddingPackage.setName(weddingPackageRequest.getName());
            weddingPackage.setDescription(weddingPackageRequest.getDescription());
            weddingPackage.setBasePrice(weddingPackageRequest.getBasePrice());
            weddingPackage.setCity(city);
            List<BonusDetail> bonusDetails = new ArrayList<>();
            if(weddingPackageRequest.getBonusDetails() != null && !weddingPackageRequest.getBonusDetails().isEmpty()) {
                for (BonusDetailRequest bonusDetailRequest: weddingPackageRequest.getBonusDetails()) {
                    // ErrorResponse
                    BonusPackage bonusPackage = bonusPackageService.loadBonusPackageById(bonusDetailRequest.getBonusPackageId());
                    int quantity;
                    if (bonusDetailRequest.getQuantity() == null) {
                        quantity = bonusPackage.getMinQuantity();
                    } else if (bonusDetailRequest.getQuantity() < bonusPackage.getMinQuantity()) {
                        throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_QUANTITY_MIN);
                    } else if (bonusDetailRequest.getQuantity() > bonusPackage.getMaxQuantity()) {
                        throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.INVALID_QUANTITY_MAX);
                    } else {
                        quantity = bonusDetailRequest.getQuantity();
                    }
                    BonusDetail bonusDetail = BonusDetail.builder()
                            .weddingPackage(weddingPackage)
                            .bonusPackage(bonusPackage)
                            .quantity(quantity)
                            .adjustable(bonusDetailRequest.isAdjustable())
                            .build();
                    bonusDetails.add(bonusDetail);
                }
            }
            weddingPackage.setBonusDetails(bonusDetails);
            weddingPackage = weddingPackageRepository.save(weddingPackage);
            WeddingPackageResponse response = WeddingPackageResponse.from(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during updating wedding package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error during updating wedding package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error during updating wedding package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteWeddingPackage(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, weddingPackage);
            weddingPackage.setDeletedAt(LocalDateTime.now());
            weddingPackageRepository.save(weddingPackage);
            return ApiResponse.success(Message.WEDDING_PACKAGE_DELETED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during deletion wedding package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting wedding package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> addWeddingPackageImage(JwtClaim userInfo, String id, MultipartFile image) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, weddingPackage);
            // ErrorResponse
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.IMAGE_IS_NULL);
            // ErrorResponse
            Image addedImage = imageService.createImage(image);
            if (weddingPackage.getImages() == null) {
                weddingPackage.setImages(new ArrayList<>());
            }
            weddingPackage.getImages().add(addedImage);
            weddingPackage = weddingPackageRepository.save(weddingPackage);
            WeddingPackageResponse response = WeddingPackageResponse.from(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during adding wedding package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during adding wedding package image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> deleteWeddingPackageImage(JwtClaim userInfo, String id, String imageId) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, weddingPackage);
            // ErrorResponse
            imageService.deleteImage(imageId);
            List<Image> images = weddingPackage.getImages();
            if (images != null) {
                images.removeIf(image -> image.getId().equals(imageId));
            }
            weddingPackage = weddingPackageRepository.save(weddingPackage);
            WeddingPackageResponse response = WeddingPackageResponse.from(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during deleting wedding package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting wedding package image: {}", e.getError());
            throw e;
        }
    }
}
