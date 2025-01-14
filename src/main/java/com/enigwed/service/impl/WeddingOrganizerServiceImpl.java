package com.enigwed.service.impl;

import com.enigwed.constant.EUserStatus;
import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.repository.spesification.SearchSpecifications;
import com.enigwed.service.*;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.StatisticUtil;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeddingOrganizerServiceImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;
    private final UserCredentialService userCredentialService;
    private final ImageService imageService;
    private final AddressService addressService;
    private final ValidationUtil validationUtil;
    private final AccessValidationUtil accessValidationUtil;
    private final StatisticUtil statisticUtil;

    private WeddingOrganizer findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_ID_IS_REQUIRED);
        return weddingOrganizerRepository.findByIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(id)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND(id)));
    }

    private List<WeddingOrganizer> filterResult(FilterRequest filter, List<WeddingOrganizer> woList) {
        return woList.stream()
                .filter(wo ->
                        (filter.getProvinceId() == null || wo.getProvince().getId().equals(filter.getProvinceId())) &&
                                (filter.getRegencyId() == null || wo.getRegency().getId().equals(filter.getRegencyId())) &&
                                (filter.getDistrictId() == null || wo.getDistrict().getId().equals(filter.getDistrictId()))
                )
                .toList();
    }

    private EUserStatus getUserStatus(WeddingOrganizer wo) {
        if (wo.getUserCredential().isActive()) {
            return EUserStatus.ACTIVE;
        } else if (wo.getDeletedAt() == null) {
            return EUserStatus.INACTIVE;
        } else {
            return EUserStatus.DELETED;
        }
    }

    private List<WeddingOrganizer> filterByStatus(FilterRequest filter, List<WeddingOrganizer> woList) {
        return woList.stream()
                .filter(wo -> filter.getUserStatus() == null || getUserStatus(wo) == filter.getUserStatus())
                .toList();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) throws DataIntegrityViolationException {
        if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizer.getPhone()) > 0) throw new DataIntegrityViolationException(SErrorMessage.PHONE_ALREADY_EXIST(weddingOrganizer.getPhone()));
        if (weddingOrganizerRepository.countByNibAndDeletedAtIsNull(weddingOrganizer.getNib()) > 0) throw new DataIntegrityViolationException(SErrorMessage.NIB_ALREADY_EXIST(weddingOrganizer.getNib()));
        if (weddingOrganizerRepository.countByNpwpAndDeletedAtIsNull(weddingOrganizer.getNpwp()) > 0) throw new DataIntegrityViolationException(SErrorMessage.NPWP_ALREADY_EXIST(weddingOrganizer.getNpwp()));
        weddingOrganizerRepository.saveAndFlush(weddingOrganizer);
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerById(String id) {
        try {
            /* FIND WEDDING ORGANIZER */
            // ErrorResponse //
            return findByIdOrThrow(id);

        } catch (ErrorResponse e) {
            log.error("Error while loading wedding organizer by ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId) {
        try {
            /* VALIDATE INPUT */
            // ErrorResponse //
            if (userCredentialId == null || userCredentialId.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.USER_CREDENTIAL_ID_IS_REQUIRED);

            /* FIND WEDDING ORGANIZER */
            // ErrorResponse //
            return weddingOrganizerRepository.findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(userCredentialId)
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));

        } catch (ErrorResponse e) {
            log.error("Error while loading wedding organizer by user credential ID: {}", e.getError());
            throw e;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerByEmail(String email) {
        /* VALIDATE INPUT */
        // ErrorResponse //
        if (email == null || email.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.EMAIL_IS_REQUIRED);

        /* FIND WEDDING ORGANIZER */
        // ErrorResponse //
        return weddingOrganizerRepository.findByUserCredentialEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND_EMAIL(email)));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void extendWeddingOrganizerSubscription(WeddingOrganizer weddingOrganizer, SubscriptionPackage subscriptionPackage) {
        /* LOAD USER CREDENTIAL */
        UserCredential user = weddingOrganizer.getUserCredential();

        /* EXTEND ACTIVE UNTIL */
        user = userCredentialService.extendActiveUntil(user, subscriptionPackage);

        /* SET AND SAVE USER CREDENTIAL */
        weddingOrganizer.setUserCredential(user);
        weddingOrganizerRepository.save(weddingOrganizer);
    }

    @Override
    public List<WeddingOrganizer> findAllWeddingOrganizers() {
        return  weddingOrganizerRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> customerFindAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* LOAD WEDDING ORGANIZERS BY KEYWORD */
            Specification<WeddingOrganizer> spec = SearchSpecifications.searchWeddingOrganizer(keyword);
            List<WeddingOrganizer> woList = weddingOrganizerRepository.findAll(spec);

            /* FILTER DELETED AND INACTIVE WEDDING ORGANIZER*/
            woList = woList.stream().filter(wo -> wo.getUserCredential().isActive() && wo.getDeletedAt() == null).toList();

            /* HANDLE EMPTY RESULT */
            if (woList.isEmpty())
                return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_ORGANIZER_FOUND);

            /* FILTER RESULT */
            woList = filterResult(filter, woList);

            /* HANDLE EMPTY RESULT */
            if (woList.isEmpty())
                return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_ORGANIZER_FOUND);

            /* MAP RESULT */
            List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::card).toList();

            /* SORT RESULT */
            responseList = responseList.stream()
                    .sorted(Comparator
                            .comparing(WeddingOrganizerResponse::getOrderFinishCount, Comparator.reverseOrder())
                            .thenComparing(WeddingOrganizerResponse::getRating, Comparator.reverseOrder())
                            .thenComparing(WeddingOrganizerResponse::getWeddingPackageCount, Comparator.reverseOrder())
                            .thenComparing(WeddingOrganizerResponse::getProductCount, Comparator.reverseOrder())
                            .thenComparing(WeddingOrganizerResponse::getActiveUntil, Comparator.reverseOrder())
                            .thenComparing(WeddingOrganizerResponse::getName)
                    )
                    .toList();

            /* RETURN RESPONSE */
            return ApiResponse.success(responseList, pagingRequest, SMessage.WEDDING_ORGANIZERS_FOUND(woList.size()));

        } catch (ValidationException e) {
            log.error("Validation error loading wedding organizers: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingOrganizerResponse> customerFindWeddingOrganizerById(String id) {
        try {
            /* FIND ACTIVE WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* MAP RESULT */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.information(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_FOUND(id));

        } catch (ErrorResponse e) {
            log.error("Error while loading wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> findOwnWeddingOrganizer(JwtClaim userInfo) {
        try {
            /* LOAD ACTIVE WEDDING ORGANIZER BY USER ID */
            WeddingOrganizer wo = weddingOrganizerRepository.findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(userInfo.getUserId())
                    .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));

            /* MAP RESULT */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_FOUND);

        } catch (ErrorResponse e) {
            log.error("Error while loading own wedding organizer info: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer(JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = findByIdOrThrow(weddingOrganizerRequest.getId());

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, wo);

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(weddingOrganizerRequest);
            // DataIntegrityViolationException //
            if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizerRequest.getPhone()) > 0 && !wo.getPhone().equals(weddingOrganizerRequest.getPhone()))
                throw new DataIntegrityViolationException(SErrorMessage.PHONE_ALREADY_EXIST(weddingOrganizerRequest.getPhone()));

            /* UPDATE WEDDING ORGANIZER */
            String phone = weddingOrganizerRequest.getPhone().startsWith("08") ?
                    String.format("+628%s", weddingOrganizerRequest.getPhone().substring(2)) :
                    weddingOrganizerRequest.getPhone();
            wo.setName(weddingOrganizerRequest.getName());
            wo.setPhone(phone);
            wo.setDescription(weddingOrganizerRequest.getDescription());
            wo.setAddress(weddingOrganizerRequest.getAddress());

            /* CREATE OR LOAD AND SET ADDRESS */
            if (!wo.getProvince().getId().equals(weddingOrganizerRequest.getProvince().getId())) {
                // ErrorResponse
                Province province = addressService.saveOrLoadProvince(weddingOrganizerRequest.getProvince());
                wo.setProvince(province);
            }
            if (!wo.getRegency().getId().equals(weddingOrganizerRequest.getRegency().getId())) {
                // ErrorResponse
                Regency regency = addressService.saveOrLoadRegency(weddingOrganizerRequest.getRegency());
                wo.setRegency(regency);
            }
            if (!wo.getDistrict().getId().equals(weddingOrganizerRequest.getDistrict().getId())) {
                // ErrorResponse
                District district = addressService.saveOrLoadDistrict(weddingOrganizerRequest.getDistrict());
                wo.setDistrict(district);
            }
            wo.setAddress(weddingOrganizerRequest.getAddress());

            /* SAVE WEDDING ORGANIZER */
            wo = weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_UPDATED(wo.getId()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error while updating wedding organizer: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error while updating wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while updating wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUserOrAdmin(userInfo, wo);

            /* SOFT DELETE WEDDING ORGANIZER */
            // ErrorResponse //
            UserCredential user = userCredentialService.deleteUser(wo.getUserCredential());
            wo.setUserCredential(user);
            wo.setDeletedAt(LocalDateTime.now());

            /* SAVE WEDDING ORGANIZER */
            weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            return ApiResponse.success(SMessage.WEDDING_ORGANIZER_DELETED(wo.getId()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, wo);

            /* VALIDATE INPUT */
            // ErrorResponse //
            if (avatar == null || avatar.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, SErrorMessage.IMAGE_IS_REQUIRED);

            /* UPDATE IMAGE */
            // ErrorResponse //
            Image newAvatar = imageService.updateImage(wo.getAvatar().getId(), avatar);
            wo.setAvatar(newAvatar);

            /* SAVE WEDDING ORGANIZER */
            wo = weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_AVATAR_UPDATED(wo.getName()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating wedding organizer image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while updating wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, wo);

            /* DELETE IMAGE */
            // ErrorResponse //
            Image deletedImage = imageService.softDeleteImageById(wo.getAvatar().getId());
            wo.setAvatar(deletedImage);

            /* SAVE WEDDING ORGANIZER */
            wo = weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_AVATAR_DELETED(wo.getName()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding organizer image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest, String keyword) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(pagingRequest);

            /* LOAD ALL WEDDING ORGANIZERS */
            Sort sort = Sort.by(Sort.Order.desc("createdAt"));
            Specification<WeddingOrganizer> spec = SearchSpecifications.searchWeddingOrganizer(keyword);
            List<WeddingOrganizer> woList = weddingOrganizerRepository.findAll(spec, sort);

            /* COUNT WEDDING ORGANIZER BY STATUS */
            Map<String, Integer> countByStatus = statisticUtil.countWeddingOrganizerByStatus(woList);
            if (woList.isEmpty()) return ApiResponse.successWeddingOrganizerList(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

            /* FILTER RESULT */
            woList = filterResult(filter, woList);
            if (woList.isEmpty())
                return ApiResponse.successWeddingOrganizerList(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

            /* RE-COUNT WEDDING ORGANIZER BY STATUS BEFORE FILTER BY STATUS */
            countByStatus = statisticUtil.countWeddingOrganizerByStatus(woList);

            /* FILTER RESULT BY STATUS */
            woList = filterByStatus(filter, woList);
            if (woList.isEmpty())
                return ApiResponse.successWeddingOrganizerList(new ArrayList<>(), pagingRequest, SMessage.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

            /* MAP RESULT */
            List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::cardAdmin).toList();

            /* RETURN RESPONSE */
            return ApiResponse.successWeddingOrganizerList(responseList, pagingRequest, SMessage.WEDDING_ORGANIZERS_FOUND(woList.size()), countByStatus);

        } catch (ValidationException e){
            log.error("Access denied while loading all wedding organizers: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id) {
        /* FIND WEDDING ORGANIZER */
        WeddingOrganizer wo = weddingOrganizerRepository.findById(id)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND(id)));

        /* MAP RESPONSE */
        WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
        return ApiResponse.success(response, SMessage.WEDDING_ORGANIZER_FOUND(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.UPDATE_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND(id)));

            /* ACTIVATE USER */
            UserCredential user = userCredentialService.activateUser(wo.getUserCredential());
            wo.setUserCredential(user);

            /* SAVE WEDDING ORGANIZER */
            weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZERS_ACTIVATED(wo.getName()));

        } catch (ErrorResponse e) {
            log.error("Error while activating wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> deactivateWeddingOrganizer(String id) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse
            WeddingOrganizer wo = weddingOrganizerRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.UPDATE_FAILED, SErrorMessage.WEDDING_ORGANIZER_NOT_FOUND(id)));

            /* DEACTIVATE USER */
            UserCredential user = userCredentialService.deactivateUser(wo.getUserCredential());
            wo.setUserCredential(user);

            /* SAVE WEDDING ORGANIZER */
            weddingOrganizerRepository.save(wo);

            /* MAP RESPONSE */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, SMessage.WEDDING_ORGANIZERS_DEACTIVATED(wo.getName()));

        } catch (ErrorResponse e) {
            log.error("Error while deactivating wedding organizer: {}", e.getError());
            throw e;
        }
    }
}
