package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BonusDetailRequest;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
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
    private final ProductService productService;
    private final ImageService imageService;
    private final AddressService addressService;
    private final ValidationUtil validationUtil;

    private WeddingPackage findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.WEDDING_PACKAGE_ID_IS_REQUIRED);
        return weddingPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_PACKAGE_NOT_FOUND));
    }

    private List<WeddingPackage> filterResult(FilterRequest filter, List<WeddingPackage> list) {
        if (filter.getWeddingOrganizerId() != null) {
            list = list.stream().filter(item -> item.getWeddingOrganizer().getId().equals(filter.getWeddingOrganizerId())).toList();
        }
        if (filter.getProvinceId() != null) {
            list = list.stream().filter(item -> item.getProvince().getId().equals(filter.getProvinceId())).toList();
        }
        if (filter.getRegencyId() != null) {
            list = list.stream().filter(item -> item.getRegency().getId().equals(filter.getRegencyId())).toList();
        }
        if (filter.getMinPrice() != null) {
            list = list.stream().filter(item -> item.getPrice() >= filter.getMinPrice()).toList();
        }
        if (filter.getMaxPrice() != null) {
            list = list.stream().filter(item -> item.getPrice() <= filter.getMaxPrice()).toList();
        }
        return list;
    }

    private void validateUserAccess(JwtClaim userInfo, WeddingPackage weddingPackage) throws AccessDeniedException {
        String userCredentialId = weddingPackage.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public WeddingPackage addOrderCount(WeddingPackage weddingPackage) {
        weddingPackage.setOrderCount(weddingPackage.getOrderCount() + 1);
        return weddingPackageRepository.saveAndFlush(weddingPackage);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingPackageResponse> customerFindWeddingPackageById(String id) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            WeddingPackageResponse response = WeddingPackageResponse.information(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> customerFindAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByDeletedAtIsNull();
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> customerSearchWeddingPackage(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.searchWeddingPackage(keyword);
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }

    @Override
    public ApiResponse<WeddingPackageResponse> getOwnWeddingPackageById(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            WeddingPackage weddingPackage = findByIdOrThrow(id);

            validateUserAccess(userInfo, weddingPackage);

            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_FOUND);

        } catch (AccessDeniedException e) {
            log.error("Access denied during loading wedding package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during loading wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<List<WeddingPackageResponse>> getOwnWeddingPackages(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> searchOwnWeddingPackages(JwtClaim userInfo, String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        // ErrorResponse
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findByWeddingOrganizerIdAndKeyword(wo.getId(), keyword);
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest) {
        try {
            // ErrorResponse
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            // ValidationException
            validationUtil.validateAndThrow(weddingPackageRequest);

            /* CREATE OR LOAD ADDRESS */
            Province province = addressService.saveOrLoadProvince(weddingPackageRequest.getProvince());
            // ErrorResponse
            Regency regency = addressService.saveOrLoadRegency(weddingPackageRequest.getRegency());

            WeddingPackage weddingPackage = WeddingPackage.builder()
                    .name(weddingPackageRequest.getName())
                    .description(weddingPackageRequest.getDescription())
                    .price(weddingPackageRequest.getPrice())
                    .province(province)
                    .regency(regency)
                    .weddingOrganizer(weddingOrganizer)
                    .build();

            /* ADD BONUS DETAILS */
            List<BonusDetail> bonusDetails = new ArrayList<>();
            if(weddingPackageRequest.getBonusDetails() != null && !weddingPackageRequest.getBonusDetails().isEmpty()) {
                for (BonusDetailRequest bonusDetailRequest: weddingPackageRequest.getBonusDetails()) {
                    // ErrorResponse
                    Product product = productService.loadProductById(bonusDetailRequest.getProductId());
                    BonusDetail bonusDetail = BonusDetail.builder()
                            .weddingPackage(weddingPackage)
                            .product(product)
                            .quantity(bonusDetailRequest.getQuantity())
                            .build();
                    bonusDetails.add(bonusDetail);
                }
            }
            weddingPackage.setBonusDetails(bonusDetails);

            weddingPackage = weddingPackageRepository.save(weddingPackage);

            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
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

            weddingPackage.setName(weddingPackageRequest.getName());
            weddingPackage.setDescription(weddingPackageRequest.getDescription());
            weddingPackage.setPrice(weddingPackageRequest.getPrice());

            /* CREATE OR LOAD ADDRESS */
            if (!weddingPackage.getProvince().getId().equals(weddingPackageRequest.getProvince().getId())) {
                // ErrorResponse
                Province province = addressService.saveOrLoadProvince(weddingPackageRequest.getProvince());
                weddingPackage.setProvince(province);
            }
            if (!weddingPackage.getRegency().getId().equals(weddingPackageRequest.getRegency().getId())) {
                // ErrorResponse
                Regency regency = addressService.saveOrLoadRegency(weddingPackageRequest.getRegency());
                weddingPackage.setRegency(regency);
            }

            List<BonusDetail> bonusDetails = new ArrayList<>();
            if(weddingPackageRequest.getBonusDetails() != null && !weddingPackageRequest.getBonusDetails().isEmpty()) {
                for (BonusDetailRequest bonusDetailRequest: weddingPackageRequest.getBonusDetails()) {
                    // ErrorResponse
                    Product product = productService.loadProductById(bonusDetailRequest.getProductId());
                    BonusDetail bonusDetail = BonusDetail.builder()
                            .weddingPackage(weddingPackage)
                            .product(product)
                            .quantity(bonusDetailRequest.getQuantity())
                            .build();
                    bonusDetails.add(bonusDetail);
                }
            }
            weddingPackage.setBonusDetails(bonusDetails);

            weddingPackage = weddingPackageRepository.save(weddingPackage);

            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
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

            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
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

            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, Message.WEDDING_PACKAGE_UPDATED);

        } catch (AccessDeniedException e) {
            log.error("Access denied during deleting wedding package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting wedding package image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.WEDDING_PACKAGE_ID_IS_REQUIRED);
        WeddingPackage weddingPackage = weddingPackageRepository.findById(id)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_PACKAGE_NOT_FOUND));
        WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
        return ApiResponse.success(response, Message.WEDDING_PACKAGE_FOUND);
    }

    @Override
    public ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.findAll();
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_PACKAGE_FOUND);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }

    @Override
    public ApiResponse<List<WeddingPackageResponse>> searchWeddingPackage(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        List<WeddingPackage> weddingPackageList = weddingPackageRepository.searchAllWeddingPackages(keyword);

        weddingPackageList = filterResult(filter, weddingPackageList);

        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, Message.WEDDING_PACKAGES_FOUND);
    }
}
