package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.EUserStatus;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
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
import java.util.List;

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
        if (filter.getUserStatus() != null) {
            if (filter.getUserStatus() == EUserStatus.ACTIVE) {
                woList = woList.stream().filter(wo -> wo.getUserCredential().isActive()).toList();
            }
            if (filter.getUserStatus() == EUserStatus.INACTIVE) {
                woList = woList.stream().filter(wo -> !wo.getUserCredential().isActive() && wo.getDeletedAt() == null).toList();
            }
            if (filter.getUserStatus() == EUserStatus.DELETED) {
                woList = woList.stream().filter(wo -> wo.getDeletedAt() != null).toList();
            }
        }
        if (filter.getProvinceId() != null) {
            woList = woList.stream().filter(wo -> wo.getProvince().getId().equals(filter.getProvinceId())).toList();
        }
        if (filter.getRegencyId() != null) {
            woList = woList.stream().filter(wo -> wo.getRegency().getId().equals(filter.getRegencyId())).toList();
        }
        if (filter.getDistrictId() != null) {
            woList = woList.stream().filter(wo -> wo.getDistrict().getId().equals(filter.getDistrictId())).toList();
        }
        return woList;
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
        /* LOAD ONLY ACTIVE WEDDING ORGANIZERS */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.findByDeletedAtIsNullAndUserCredentialActiveIsTrue();
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::information).toList();
        return ApiResponse.success(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> customerSearchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        /* SEARCH ACTIVE WEDDING ORGANIZERS BY KEYWORD */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.searchWeddingOrganizerCustomer(keyword);
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);

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
        wo.setUserCredential(user);

        /* SAVE WEDDING ORGANIZER */
        weddingOrganizerRepository.save(wo);

        WeddingOrganizerResponse response = WeddingOrganizerResponse.all(wo);
        return ApiResponse.success(response, Message.WEDDING_ORGANIZERS_ACTIVATED);
    }

    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers(FilterRequest filter, PagingRequest pagingRequest) {
        /* LOAD ALL WEDDING ORGANIZERS */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.findAll();
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::simpleAdmin).toList();
        return ApiResponse.success(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword, FilterRequest filter, PagingRequest pagingRequest) {
        /* SEARCH ALL WEDDING ORGANIZERS BY KEYWORD */
        List<WeddingOrganizer> woList = weddingOrganizerRepository.searchWeddingOrganizer(keyword);
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);

        /* FILTER RESULT */
        woList = filterResult(filter, woList);

        /* MAP RESULT */
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::simpleAdmin).toList();
        return ApiResponse.success(responseList, pagingRequest, Message.WEDDING_ORGANIZERS_FOUND);
    }
}
