package com.enigwed.service.impl;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
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

    private City findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, Message.ID_IS_REQUIRED);
        return cityRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.CITY_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<City> create(CityRequest cityRequest) {
        Image thumbnail = imageService.create(cityRequest.getThumbnail());

        City city = cityRepository.findByNameAndDeletedAtIsNull(cityRequest.getName()).orElse(new City());

        city.setName(cityRequest.getName());
        city.setDescription(cityRequest.getDescription());
        city.setThumbnail(thumbnail);

        city = cityRepository.save(city);

        return ApiResponse.success(city, Message.CITY_CREATED);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<City> findById(String id) {
        City city = findByIdOrThrow(id);

        return ApiResponse.success(city, Message.CITY_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<City> findByName(String name) {
        if (name == null || name.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, Message.NAME_IS_REQUIRED);
        City city = cityRepository.findByNameAndDeletedAtIsNull(name).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, Message.CITY_NOT_FOUND));

        return ApiResponse.success(city, Message.CITY_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<City>> findAll() {
        List<City> cityList = cityRepository.findByDeletedAtIsNull();

        if (cityList == null || cityList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.CITIES_FOUND);
        }

        return ApiResponse.success(cityList, Message.CITIES_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<City> update(CityRequest cityRequest) {
        City city = findByIdOrThrow(cityRequest.getId());

        city.setName(cityRequest.getName());
        city.setDescription(cityRequest.getDescription());

        if (cityRequest.getThumbnail() != null) {
            Image image = imageService.update(city.getThumbnail().getId(), cityRequest.getThumbnail());
            city.setThumbnail(image);
        }

        city = cityRepository.save(city);

        return ApiResponse.success(city, Message.CITY_UPDATED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteById(String id) {
        City city = findByIdOrThrow(id);

        city.setDeletedAt(LocalDateTime.now());

        cityRepository.save(city);

        return ApiResponse.success(Message.CITY_DELETED);
    }

    @Override
    public ApiResponse<CityResponse> testGetCityByID(String id) {
        City city = findByIdOrThrow(id);

        Resource thumbnail = imageService.findById(city.getThumbnail().getId());
        System.out.println("THUMBNAIL NAME: " + thumbnail.getFilename());

        CityResponse response = CityResponse.from(city, thumbnail);

        return ApiResponse.success(response, Message.CITY_FOUND);
    }
}
