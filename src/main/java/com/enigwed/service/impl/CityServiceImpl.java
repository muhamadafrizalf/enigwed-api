package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.CityResponse;
import com.enigwed.entity.City;
import com.enigwed.entity.Image;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.CityRepository;
import com.enigwed.service.CityService;
import com.enigwed.service.ImageService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;

    private City findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return cityRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.CITY_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    @Override
    public City loadCityById(String id) {
        try {
            // ErrorResponse
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error during loading city: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CityResponse> findCityById(String id) {
        try {
            // ErrorResponse
            City city = findByIdOrThrow(id);
            CityResponse response = CityResponse.from(city);
            return ApiResponse.success(response, Message.CITY_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading city: {}", e.getMessage());
            throw e;
        }

    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CityResponse> findCityByName(String name) {
        try {
            // ErrorResponse (Don't catch)
            if (name == null || name.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.NAME_IS_REQUIRED);
            // ErrorResponse (Don't catch)
            City city = cityRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.CITY_NOT_FOUND));
            CityResponse response = CityResponse.from(city);
            return ApiResponse.success(response, Message.CITY_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading city: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<CityResponse>> findAllCity() {
        List<City> cityList = cityRepository.findByDeletedAtIsNull();
        if (cityList == null || cityList.isEmpty()) return ApiResponse.success(new ArrayList<>(), Message.NO_CITY_FOUND);
        List<CityResponse> cityResponseList = cityList.stream().map(CityResponse::from).toList();
        return ApiResponse.success(cityResponseList, Message.CITIES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CityResponse> createCity(CityRequest cityRequest) {
        try {
            // DataIntegrityViolationException
            if (cityRepository.findByNameAndDeletedAtIsNull(cityRequest.getName()).isPresent()) throw new DataIntegrityViolationException(ErrorMessage.NAME_UNIQUE);
            // ErrorResponse
            if (cityRequest.getThumbnail() == null || cityRequest.getThumbnail().isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.IMAGE_IS_NULL);
            // ValidationException
            validationUtil.validateAndThrow(cityRequest);
            // ErrorResponse
            Image thumbnail = imageService.createImage(cityRequest.getThumbnail());
            City city = City.builder()
                    .name(cityRequest.getName())
                    .description(cityRequest.getDescription())
                    .thumbnail(thumbnail)
                    .build();
            city = cityRepository.save(city);
            CityResponse response = CityResponse.from(city);
            return ApiResponse.success(response, Message.CITY_CREATED);
        } catch (ValidationException e) {
            log.error("Validation error during creation city: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation error during creation city: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during creation city: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CityResponse> updateCity(CityRequest cityRequest) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(cityRequest);
            // ErrorResponse (Don't catch)
            City city = findByIdOrThrow(cityRequest.getId());
            City possibleConflict = cityRepository.findByNameAndDeletedAtIsNull(cityRequest.getName()).orElse(null);
            // DataIntegrityViolationException
            if (possibleConflict != null && !possibleConflict.getId().equals(cityRequest.getId())) {
                throw new DataIntegrityViolationException(ErrorMessage.NAME_UNIQUE);
            }
            city.setName(cityRequest.getName());
            city.setDescription(cityRequest.getDescription());
            if (cityRequest.getThumbnail() != null) {
                // ErrorResponse (Don't catch)
                Image image = imageService.updateImage(city.getThumbnail().getId(), cityRequest.getThumbnail());
                city.setThumbnail(image);
            }
            city = cityRepository.save(city);
            CityResponse response = CityResponse.from(city);
            return ApiResponse.success(response, Message.CITY_UPDATED);
        } catch (ValidationException e) {
            log.error("Validation error during update city: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, e.getErrors().get(0));
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation error during update city: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during update city: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteCity(String id) {
        try {
            // ErrorResponse
            City city = findByIdOrThrow(id);
            city.setDeletedAt(LocalDateTime.now());
            cityRepository.save(city);
            return ApiResponse.success(Message.CITY_DELETED);
        } catch (ErrorResponse e) {
            log.error("Error during deletion city: {}", e.getMessage());
            throw e;
        }

    }
}
