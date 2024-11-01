package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.CityResponse;
import com.enigwed.entity.City;
import com.enigwed.entity.Image;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.CityRepository;
import com.enigwed.service.CityService;
import com.enigwed.service.ImageService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public City loadCityById(String id) {
        return findByIdOrThrow(id);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CityResponse> create(CityRequest cityRequest) {
        if (cityRepository.findByNameAndDeletedAtIsNull(cityRequest.getName()).isPresent()) throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, ErrorMessage.NAME_UNIQUE);
        if (cityRequest.getThumbnail() == null || cityRequest.getThumbnail().isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.CREATE_FAILED, ErrorMessage.IMAGE_IS_NULL);
        validationUtil.validateAndThrow(cityRequest);

        Image thumbnail = imageService.createImage(cityRequest.getThumbnail());

        City city = City.builder()
                .name(cityRequest.getName())
                .description(cityRequest.getDescription())
                .thumbnail(thumbnail)
                .build();

        city = cityRepository.save(city);

        CityResponse response = CityResponse.from(city);
        return ApiResponse.success(response, Message.CITY_CREATED);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CityResponse> findById(String id) {
        City city = findByIdOrThrow(id);

        CityResponse response = CityResponse.from(city);
        return ApiResponse.success(response, Message.CITY_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<CityResponse> findByName(String name) {
        if (name == null || name.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.NAME_IS_REQUIRED);
        City city = cityRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.CITY_NOT_FOUND));

        CityResponse response = CityResponse.from(city);
        return ApiResponse.success(response, Message.CITY_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<CityResponse>> findAll() {
        List<City> cityList = cityRepository.findByDeletedAtIsNull();

        if (cityList == null || cityList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.CITIES_FOUND);
        }

        List<CityResponse> cityResponseList = cityList.stream().map(CityResponse::from).toList();
        return ApiResponse.success(cityResponseList, Message.CITIES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<CityResponse> update(CityRequest cityRequest) {
        validationUtil.validateAndThrow(cityRequest);

        City city = findByIdOrThrow(cityRequest.getId());

        City possibleConflict = cityRepository.findByNameAndDeletedAtIsNull(cityRequest.getName()).orElse(null);
        if (possibleConflict != null && !possibleConflict.getId().equals(cityRequest.getId())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, ErrorMessage.NAME_UNIQUE);
        }

        city.setName(cityRequest.getName());
        city.setDescription(cityRequest.getDescription());

        if (cityRequest.getThumbnail() != null) {
            Image image = imageService.update(city.getThumbnail().getId(), cityRequest.getThumbnail());
            city.setThumbnail(image);
        }

        city = cityRepository.save(city);

        CityResponse response = CityResponse.from(city);
        return ApiResponse.success(response, Message.CITY_UPDATED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteById(String id) {
        City city = findByIdOrThrow(id);

        city.setDeletedAt(LocalDateTime.now());

        cityRepository.save(city);

        return ApiResponse.success(Message.CITY_DELETED);
    }
}
