package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.WeddingOrganizerRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.WeddingOrganizerResponse;
import com.enigwed.entity.City;
import com.enigwed.entity.Image;
import com.enigwed.entity.UserCredential;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.WeddingOrganizerRepository;
import com.enigwed.service.CityService;
import com.enigwed.service.ImageService;
import com.enigwed.service.UserCredentialService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeddingOrganizerServiceImpl implements WeddingOrganizerService {
    private final WeddingOrganizerRepository weddingOrganizerRepository;
    private final CityService cityService;
    private final UserCredentialService userCredentialService;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;

    private WeddingOrganizer findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return weddingOrganizerRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createWeddingOrganizer(WeddingOrganizer weddingOrganizer) {
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
            log.error("Error during loading wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public WeddingOrganizer loadWeddingOrganizerByUserCredentialId(String userCredentialId) {
        if (userCredentialId == null || userCredentialId.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return weddingOrganizerRepository.findByUserCredentialIdAndDeletedAtIsNull(userCredentialId).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.WEDDING_ORGANIZER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<WeddingOrganizerResponse> findWeddingOrganizerById(String id) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);
            WeddingOrganizerResponse response = WeddingOrganizerResponse.from(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> findAllWeddingOrganizers() {
        List<WeddingOrganizer> woList = weddingOrganizerRepository.findByDeletedAtIsNull();
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::from).toList();
        return ApiResponse.success(responseList, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<WeddingOrganizerResponse>> searchWeddingOrganizer(String keyword) {
        List<WeddingOrganizer> woList = weddingOrganizerRepository.searchWeddingOrganizer(keyword);
        if (woList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_WEDDING_ORGANIZER_FOUND);
        List<WeddingOrganizerResponse> responseList = woList.stream().map(WeddingOrganizerResponse::from).toList();
        return ApiResponse.success(responseList, Message.WEDDING_ORGANIZERS_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizer(JwtClaim userInfo, WeddingOrganizerRequest weddingOrganizerRequest) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(weddingOrganizerRequest.getId());
            // ErrorResponse
            if (!userInfo.getUserId().equals(wo.getUserCredential().getId()) && !userInfo.getRole().equals("ROLE_ADMIN"))
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.UPDATE_FAILED, ErrorMessage.ACCESS_DENIED);
            // ValidationException
            validationUtil.validateAndThrow(weddingOrganizerRequest);
            // DataIntegrityViolationException
            if (weddingOrganizerRepository.countByPhoneAndDeletedAtIsNull(weddingOrganizerRequest.getPhone()) > 0 && !wo.getPhone().equals(weddingOrganizerRequest.getPhone()))
                throw new DataIntegrityViolationException(ErrorMessage.PHONE_ALREADY_EXIST);
            wo.setName(weddingOrganizerRequest.getName());
            wo.setPhone(weddingOrganizerRequest.getPhone());
            wo.setDescription(weddingOrganizerRequest.getDescription());
            wo.setAddress(weddingOrganizerRequest.getAddress());
            if (!wo.getCity().getId().equals(weddingOrganizerRequest.getCityId())) {
                // ErrorResponse
                City city = cityService.loadCityById(weddingOrganizerRequest.getCityId());
                wo.getCity().setId(city.getId());
            }
            wo = weddingOrganizerRepository.saveAndFlush(wo);
            WeddingOrganizerResponse response = WeddingOrganizerResponse.from(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_UPDATED);
        } catch (ValidationException e) {
            log.error("Validation error during update wedding organizer: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.REGISTER_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity error during update wedding organizer: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.REGISTER_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during update wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteWeddingOrganizer(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);
            // ErrorResponse
            if (!userInfo.getUserId().equals(wo.getUserCredential().getId()) && !userInfo.getRole().equals("ROLE_ADMIN"))
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.DELETE_FAILED, ErrorMessage.ACCESS_DENIED);
            // ErrorResponse
            wo.setUserCredential(userCredentialService.deleteUser(wo.getUserCredential().getId()));
            wo.setDeletedAt(LocalDateTime.now());
            return ApiResponse.success(Message.WEDDING_ORGANIZER_DELETED);
        } catch (ErrorResponse e) {
            log.error("Error during deletion wedding organizer: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> updateWeddingOrganizerImage(JwtClaim userInfo, String id, MultipartFile avatar) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);
            // ErrorResponse
            if (avatar == null || avatar.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.UPDATE_FAILED, ErrorMessage.IMAGE_IS_NULL);
            // ErrorResponse
            if (!userInfo.getUserId().equals(wo.getUserCredential().getId()) && !userInfo.getRole().equals("ROLE_ADMIN"))
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.UPDATE_FAILED, ErrorMessage.ACCESS_DENIED);
            // ErrorResponse
            Image newAvatar = imageService.updateImage(wo.getAvatar().getId(), avatar);
            wo.setAvatar(newAvatar);
            wo = weddingOrganizerRepository.saveAndFlush(wo);
            WeddingOrganizerResponse response = WeddingOrganizerResponse.from(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_AVATAR_UPDATED);
        } catch (ErrorResponse e) {
            log.error("Error during update wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> deleteWeddingOrganizerImage(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            WeddingOrganizer wo = findByIdOrThrow(id);
            // ErrorResponse
            if (!userInfo.getUserId().equals(wo.getUserCredential().getId()) && !userInfo.getRole().equals("ROLE_ADMIN"))
                throw new ErrorResponse(HttpStatus.UNAUTHORIZED, Message.DELETE_FAILED, ErrorMessage.ACCESS_DENIED);
            // ErrorResponse
            Image deletedImage = imageService.softDeleteImageById(wo.getAvatar().getId());
            wo.setAvatar(deletedImage);
            WeddingOrganizerResponse response = WeddingOrganizerResponse.from(wo);
            return ApiResponse.success(response, Message.WEDDING_ORGANIZER_AVATAR_DELETED);
        } catch (ErrorResponse e) {
            log.error("Error during deletion wedding organizer image: {}", e.getError());
            throw e;
        }
    }

    @Override
    public ApiResponse<WeddingOrganizerResponse> activateWeddingOrganizer(String id) {
        // ErrorResponse
        WeddingOrganizer wo = findByIdOrThrow(id);
        UserCredential user = userCredentialService.activateUser(wo.getUserCredential());
        wo.setUserCredential(user);
        weddingOrganizerRepository.saveAndFlush(wo);
        WeddingOrganizerResponse response = WeddingOrganizerResponse.from(wo);
        return ApiResponse.success(response, Message.WEDDING_ORGANIZERS_ACTIVATED);
    }
}
