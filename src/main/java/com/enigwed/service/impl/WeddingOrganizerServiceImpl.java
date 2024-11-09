package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.EUserStatus;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.BankAccountRequest;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.service.*;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeddingOrganizerServiceImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;
    private final UserCredentialService userCredentialService;
    private final ImageService imageService;
    private final AddressService addressService;
    private final ValidationUtil validationUtil;

    private WeddingOrganizer findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_ID_IS_REQUIRED);
        return weddingOrganizerRepository.findByIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(id)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
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

    private Map<String, Integer> countByStatus(List<WeddingOrganizer> woList) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (EUserStatus status : EUserStatus.values()) {
            map.put(status.name(), 0);
        }

        for (WeddingOrganizer wo : woList) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(getUserStatus(wo).name(), map.get(getUserStatus(wo).name()) + 1);
        }
        return map;
    }

    private void validateUserAccess(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    private void validateUserAccessDelete(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) throws DataIntegrityViolationException {
        // Catch in AuthService
        if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizer.getPhone()) > 0) throw new DataIntegrityViolationException(ErrorMessage.PHONE_ALREADY_EXIST);
        if (weddingOrganizerRepository.countByNibAndDeletedAtIsNull(weddingOrganizer.getNib()) > 0) throw new DataIntegrityViolationException(ErrorMessage.NIB_ALREADY_EXIST);
        if (weddingOrganizerRepository.countByNpwpAndDeletedAtIsNull(weddingOrganizer.getNpwp()) > 0) throw new DataIntegrityViolationException(ErrorMessage.NPWP_ALREADY_EXIST);
        weddingOrganizerRepository.saveAndFlush(weddingOrganizer);
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerById(String id) {
        try {
            // ErrorResponse
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error while loading wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId) {
        if (userCredentialId == null || userCredentialId.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return weddingOrganizerRepository.findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(userCredentialId)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerByEmail(String email) {
        if (email == null || email.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.EMAIL_IS_REQUIRED);
        return weddingOrganizerRepository.findByUserCredentialEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void extendWeddingOrganizerSubscription(WeddingOrganizer weddingOrganizer, SubscriptionPacket subscriptionPacket) {
        UserCredential user = weddingOrganizer.getUserCredential();
        user.setActiveUntil(user.getActiveUntil().plusMonths(subscriptionPacket.getSubscriptionLength().getMonths()));
        weddingOrganizer.setUserCredential(user);
        weddingOrganizerRepository.save(weddingOrganizer);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingOrganizerResponse> customerFindWeddingOrganizerById(String id) {
        try {
            /* FIND ACTIVE WEDDING ORGANIZER */
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* MAP RESULT */
            WeddingOrganizerResponse response = WeddingOrganizerResponse.information(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error while loading wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> customerFindAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        /* LOAD ONLY ACTIVE WEDDING ORGANIZERS */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.findByDeletedAtIsNullAndUserCredentialActiveIsTrue();
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::information).toList();
        return ApiResponse.success(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> customerSearchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        /* SEARCH ACTIVE WEDDING ORGANIZERS BY KEYWORD */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.searchWeddingOrganizerCustomer(keyword);
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::information).toList();
        return ApiResponse.success(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> getOwnWeddingOrganizer(JwtClaim userInfo) {
        /* LOAD ACTIVE WEDDING ORGANIZER BY USER ID */
        WeddingOrganizer wo = weddingOrganizerRepository.findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(userInfo.getUserId())
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));

        /* MAP RESULT */
        WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
        return ApiResponse.success(response, Message.WEDDING_ORGANIZER_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer(JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(weddingOrganizerRequest.getId());

            /* VALIDATE ACCESS */
            // AccessDeniedException
            validateUserAccess(userInfo, wo);

            /* VALIDATE INPUT */
            // ValidationException
            validationUtil.validateAndThrow(weddingOrganizerRequest);
            // DataIntegrityViolationException
            if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizerRequest.getPhone()) > 0 && !wo.getPhone().equals(weddingOrganizerRequest.getPhone()))
                throw new DataIntegrityViolationException(ErrorMessage.PHONE_ALREADY_EXIST);

            /* UPDATE WEDDING ORGANIZER */
            wo.setName(weddingOrganizerRequest.getName());
            wo.setPhone(weddingOrganizerRequest.getPhone());
            wo.setDescription(weddingOrganizerRequest.getDescription());
            wo.setAddress(weddingOrganizerRequest.getAddress());

            /* CREATE OR LOAD ADDRESS */
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
            wo = weddingOrganizerRepository.saveAndFlush(wo);

            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_UPDATED);

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error while updating wedding organizer: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error while updating wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.REGISTER_FAILED, e.getMessage());
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
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException
            validateUserAccessDelete(userInfo, wo);

            /* SOFT DELETE WEDDING ORGANIZER */
            // ErrorResponse
            wo.setUserCredential(userCredentialService.deleteUser(wo.getUserCredential().getId()));
            wo.setDeletedAt(LocalDateTime.now());

            /* SAVE WEDDING ORGANIZER */
            weddingOrganizerRepository.save(wo);

            return ApiResponse.success(Message.WEDDING_ORGANIZER_DELETED);

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException
            validateUserAccess(userInfo, wo);

            /* VALIDATE INPUT */
            // ErrorResponse
            if (avatar == null || avatar.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.IMAGE_IS_NULL);

            /* UPDATE IMAGE */
            // ErrorResponse
            Image newAvatar = imageService.updateImage(wo.getAvatar().getId(), avatar);
            wo.setAvatar(newAvatar);

            /* SAVE WEDDING ORGANIZER */
            wo = weddingOrganizerRepository.save(wo);

            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_AVATAR_UPDATED);

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating wedding organizer image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while updating wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    // ADMIN

    @Override
    public ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException
            validateUserAccess(userInfo, wo);

            /* DELETE IMAGE */
            // ErrorResponse
            Image deletedImage = imageService.softDeleteImageById(wo.getAvatar().getId());
            wo.setAvatar(deletedImage);

            /* SAVE WEDDING ORGANIZER */
            wo = weddingOrganizerRepository.save(wo);

            WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_AVATAR_DELETED);

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting wedding organizer image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id) {
        WeddingOrganizer wo = weddingOrganizerRepository.findById(id)
                .orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
        WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
        return ApiResponse.success(response, Message.WEDDING_ORGANIZER_FOUND);
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id) {
        /* LOAD WEDDING ORGANIZER */
        // ErrorResponse
        WeddingOrganizer wo = weddingOrganizerRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.UPDATE_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));

        /* ACTIVATE USER */
        UserCredential user = userCredentialService.activateUser(wo.getUserCredential());

        /* SET ACTIVE UNTIL FIRST DAY OF NEXT MOTH */
        user.setActiveUntil(LocalDateTime.now()
                .plusMonths(1)
                .withDayOfMonth(1)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
        );

        /* SAVE USER CREDENTIAL */
        wo.setUserCredential(user);

        /* SAVE WEDDING ORGANIZER */
        weddingOrganizerRepository.save(wo);

        WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
        return ApiResponse.success(response, Message.WEDDING_ORGANIZERS_ACTIVATED);
    }

    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        /* LOAD ALL WEDDING ORGANIZERS */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.findAll();
        Map<String, Integer> countByStatus = countByStatus(woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        countByStatus = countByStatus(woList);
        woList = filterByStatus(filter, woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::simpleAdmin).toList();
        return ApiResponse.successWo(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND, countByStatus);
    }

    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        /* SEARCH ALL WEDDING ORGANIZERS BY KEYWORD */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.searchWeddingOrganizer(keyword);
        Map<String, Integer> countByStatus = countByStatus(woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        countByStatus = countByStatus(woList);
        woList = filterByStatus(filter, woList);
        if (woList.isEmpty()) return ApiResponse.successWo(new ArrayList<>(), pagingRequest, Message.NO_WEDDING_ORGANIZER_FOUND, countByStatus);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::simpleAdmin).toList();
        return ApiResponse.successWo(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND, countByStatus);
    }
}
