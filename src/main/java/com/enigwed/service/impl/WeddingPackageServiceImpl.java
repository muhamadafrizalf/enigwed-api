package com.enigwed.service.impl;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
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
import com.enigwed.repository.spesification.SearchSpecifications;
import com.enigwed.service.*;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final AccessValidationUtil accessValidationUtil;

    private WeddingPackage findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_PACKAGE_ID_IS_REQUIRED);
        return weddingPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_PACKAGE_NOT_FOUND(id)));
    }

    private ApiResponse<List<WeddingPackageResponse>> getListApiResponse(FilterRequest filter, PagingRequest pagingRequest, List<WeddingPackage> weddingPackageList) {
        if (weddingPackageList == null || weddingPackageList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_PACKAGE_FOUND);

        /* FILTER RESULT */
        weddingPackageList = filterResult(filter, weddingPackageList);

        if (pagingRequest == null) {
            pagingRequest = new PagingRequest(1, weddingPackageList.size());
        }

        /* MAP AND SORT RESULT */
        List<WeddingPackageResponse> responses = weddingPackageList.stream().map(WeddingPackageResponse::card)
                .sorted(Comparator
                        .comparing(WeddingPackageResponse::getOrderCount, Comparator.reverseOrder())
                        .thenComparing(WeddingPackageResponse::getRating, Comparator.reverseOrder())
                        .thenComparing(weddingPackageResponse -> weddingPackageResponse.getWeddingOrganizer().getRating(), Comparator.reverseOrder())
                        .thenComparing(weddingPackageResponse -> weddingPackageResponse.getWeddingOrganizer().getActiveUntil(), Comparator.reverseOrder())
                        .thenComparing(WeddingPackageResponse::getName)
                )
                .toList();

        return ApiResponse.success(responses, pagingRequest, SMessage.WEDDING_PACKAGES_FOUND(weddingPackageList.size()));
    }

    private List<WeddingPackage> filterResult(FilterRequest filter, List<WeddingPackage> list) {
        return list.stream()
                .filter(item -> (filter.getWeddingOrganizerId() == null || item.getWeddingOrganizer().getId().equals(filter.getWeddingOrganizerId())) &&
                        (filter.getProvinceId() == null || item.getProvince().getId().equals(filter.getProvinceId())) &&
                        (filter.getRegencyId() == null || item.getRegency().getId().equals(filter.getRegencyId())) &&
                        (filter.getMinPrice() == null || item.getPrice() >= filter.getMinPrice()) &&
                        (filter.getMaxPrice() == null || item.getPrice() <= filter.getMaxPrice())
                )
                .toList();
    }

    private void setBonusDetails(WeddingPackageRequest weddingPackageRequest, WeddingPackage weddingPackage) {
        List<BonusDetail> bonusDetails = new ArrayList<>();
        if(weddingPackageRequest.getBonusDetails() != null && !weddingPackageRequest.getBonusDetails().isEmpty()) {
            for (BonusDetailRequest bonusDetailRequest: weddingPackageRequest.getBonusDetails()) {
                /* LOAD PRODUCT */
                // ErrorResponse //
                Product product = productService.loadProductById(bonusDetailRequest.getProductId());

                /* VALIDATE PRODUCT */
                // ErrorResponse //
                if (!product.getWeddingOrganizer().getId().equals(weddingPackage.getWeddingOrganizer().getId()))
                    throw new  ErrorResponse(HttpStatus.FORBIDDEN, SMessage.CREATE_FAILED, SErrorMessage.PRODUCT_FORBIDDEN);

                /* CREATE AND ADD BONUS DETAIL */
                BonusDetail bonusDetail = BonusDetail.builder()
                        .weddingPackage(weddingPackage)
                        .product(product)
                        .quantity(bonusDetailRequest.getQuantity())
                        .build();
                bonusDetails.add(bonusDetail);
            }
        }
        weddingPackage.setBonusDetails(bonusDetails);
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingPackage loadWeddingPackageById(String id) {
        try {
            /* FIND WEDDING PACKAGE */
            // ErrorResponse //
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error while loading wedding package by ID: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void addOrderCount(WeddingPackage weddingPackage) {
        weddingPackage.setOrderCount(weddingPackage.getOrderCount() + 1);
        weddingPackageRepository.saveAndFlush(weddingPackage);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> customerFindAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE PAGING REQUEST */
            if (pagingRequest != null) {
                validationUtil.validateAndThrow(pagingRequest);
            }

            /* FIND WEDDING PACKAGES */
            Specification<WeddingPackage> spec = SearchSpecifications.searchWeddingPackage(keyword);
            List<WeddingPackage> weddingPackageList = weddingPackageRepository.findAll(spec);
            weddingPackageList = weddingPackageList.stream().filter(item -> item.getDeletedAt() == null && item.getWeddingOrganizer().getUserCredential().isActive()).toList();

            /* MAP RESPONSE */
            return getListApiResponse(filter, pagingRequest, weddingPackageList);

        } catch (ValidationException e) {
            log.error("Validation error while finding wedding packages: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while finding wedding packages: {}", e.getError());
            throw e;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingPackageResponse> customerFindWeddingPackageById(String id) {
        try {
            /* FIND WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            WeddingPackageResponse response = WeddingPackageResponse.information(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_FOUND(id));
        } catch (ErrorResponse e) {
            log.error("Error while finding wedding package by ID: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<List<WeddingPackageResponse>> findOwnWeddingPackages(JwtClaim userInfo, FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND WEDDING PACKAGES */
            Specification<WeddingPackage> spec = SearchSpecifications.searchWeddingPackage(keyword);
            List<WeddingPackage> weddingPackageList = weddingPackageRepository.findAll(spec);
            weddingPackageList = weddingPackageList.stream().filter(item -> item.getDeletedAt() == null && item.getWeddingOrganizer().getId().equals(wo.getId())).toList();

            /* MAP RESPONSE */
            return getListApiResponse(filter, pagingRequest, weddingPackageList);

        } catch (ValidationException e) {
            log.error("Validation error while finding own wedding packages: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while finding own wedding packages: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingPackageResponse> findOwnWeddingPackageById(JwtClaim userInfo, String id) {
        try {
            /* FIND WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, weddingPackage.getWeddingOrganizer());

            /* MAP RESPONSE */
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_FOUND(id));

        } catch (AccessDeniedException e) {
            log.error("Access denied while finding own wedding package by ID: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.FETCHING_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while finding own wedding package by ID: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> createWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* CHECK IF WEDDING ORGANIZER HAS ANY BANK ACCOUNT */
            // ErrorResponse //
            if (weddingOrganizer.getBankAccounts() == null || weddingOrganizer.getBankAccounts().isEmpty())
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, SErrorMessage.BANK_ACCOUNT_EMPTY(weddingOrganizer.getName()));

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(weddingPackageRequest);

            /* CREATE OR LOAD ADDRESS */
            // ErrorResponse //
            Province province = addressService.saveOrLoadProvince(weddingPackageRequest.getProvince());
            // ErrorResponse //
            Regency regency = addressService.saveOrLoadRegency(weddingPackageRequest.getRegency());

            /* CREATE WEDDING PACKAGE */
            WeddingPackage weddingPackage = WeddingPackage.builder()
                    .name(weddingPackageRequest.getName())
                    .description(weddingPackageRequest.getDescription())
                    .price(weddingPackageRequest.getPrice())
                    .province(province)
                    .regency(regency)
                    .weddingOrganizer(weddingOrganizer)
                    .build();

            /* ADD BONUS DETAILS */
            setBonusDetails(weddingPackageRequest, weddingPackage);

            /* SAVE WEDDING PACKAGE */
            weddingPackage = weddingPackageRepository.save(weddingPackage);

            /* MAP RESPONSE */
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_CREATED(weddingPackage.getId()));

        } catch (ValidationException e) {
            log.error("Validation error while creating wedding package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while creating wedding package: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> updateWeddingPackage(JwtClaim userInfo, WeddingPackageRequest weddingPackageRequest) {
        try {
            /* LOAD WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(weddingPackageRequest.getId());

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, weddingPackage.getWeddingOrganizer());

            /* VALIDATE INPUT */
            // ValidationException //
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

            /* ADD BONUS DETAILS */
            setBonusDetails(weddingPackageRequest, weddingPackage);

            /* SAVE WEDDING PACKAGE */
            weddingPackage = weddingPackageRepository.save(weddingPackage);

            /* MAP RESPONSE */
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_UPDATED(weddingPackage.getId()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating wedding package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error while updating wedding package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while updating wedding package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteWeddingPackage(JwtClaim userInfo, String id) {
        try {
            /* LOAD WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUserOrAdmin(userInfo, weddingPackage.getWeddingOrganizer());

            /* SET DELETED AT */
            weddingPackage.setDeletedAt(LocalDateTime.now());

            /* SAVE WEDDING PACKAGE */
            weddingPackageRepository.save(weddingPackage);

            /* MAP RESPONSE */
            return ApiResponse.success(SMessage.WEDDING_PACKAGE_DELETED(id));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding package: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> addWeddingPackageImage(JwtClaim userInfo, String id, MultipartFile image) {
        try {
            /* LOAD WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, weddingPackage.getWeddingOrganizer());

            /* VALIDATE INPUT */
            // ErrorResponse //
            if (image == null) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, SErrorMessage.IMAGE_IS_NULL);

            /* CREATE AND ADD IMAGE */
            // ErrorResponse //
            Image addedImage = imageService.createImage(image);
            if (weddingPackage.getImages() == null) {
                weddingPackage.setImages(new ArrayList<>());
            }
            weddingPackage.getImages().add(addedImage);

            /* SAVE WEDDING PACKAGE */
            weddingPackage = weddingPackageRepository.save(weddingPackage);

            /* MAP RESPONSE */
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_IMAGE_ADDED(weddingPackage.getName()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while adding wedding package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while adding wedding package image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingPackageResponse> deleteWeddingPackageImage(JwtClaim userInfo, String id, String imageId) {
        try {
            /* LOAD WEDDING PACKAGE */
            // ErrorResponse //
            WeddingPackage weddingPackage = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, weddingPackage.getWeddingOrganizer());

            /* DELETE IMAGE */
            // ErrorResponse //
            imageService.deleteImage(imageId);
            List<Image> images = weddingPackage.getImages();
            if (images != null) {
                images.removeIf(image -> image.getId().equals(imageId));
            }

            /* SAVE WEDDING PACKAGE */
            weddingPackage = weddingPackageRepository.save(weddingPackage);

            /* MAP RESPONSE */
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_IMAGE_DELETED(weddingPackage.getName(), imageId));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding package image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding package image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingPackageResponse>> findAllWeddingPackages(FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* FIND WEDDING PACKAGES */
            Specification<WeddingPackage> spec = SearchSpecifications.searchWeddingPackage(keyword);
            List<WeddingPackage> weddingPackageList = weddingPackageRepository.findAll(spec);
            weddingPackageList = weddingPackageList.stream().filter(item -> item.getDeletedAt() == null).toList();

            /* MAP RESPONSE */
            return getListApiResponse(filter, pagingRequest, weddingPackageList);

        }  catch (ValidationException e) {
            log.error("Validation error while finding all wedding packages: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while finding all wedding packages: {}", e.getError());
            throw e;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingPackageResponse> findWeddingPackageById(String id) {
        try {
            WeddingPackage weddingPackage = findByIdOrThrow(id);
            WeddingPackageResponse response = WeddingPackageResponse.all(weddingPackage);
            return ApiResponse.success(response, SMessage.WEDDING_PACKAGE_FOUND(id));
        } catch (ErrorResponse e) {
            log.error("Error while finding all wedding package by ID: {}", e.getError());
            throw e;
        }
    }

}
