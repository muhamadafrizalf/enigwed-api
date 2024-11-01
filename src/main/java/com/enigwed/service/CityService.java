package com.enigwed.service;

import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.CityResponse;
import com.enigwed.entity.City;

import java.util.List;

public interface CityService {
    City loadCityById(String id);

    ApiResponse<CityResponse> createCity(CityRequest cityRequest);
    ApiResponse<CityResponse> findCityById(String id);
    ApiResponse<CityResponse> findCityByName(String name);
    ApiResponse<List<CityResponse>> findAllCity();
    ApiResponse<CityResponse> updateCity(CityRequest cityRequest);
    ApiResponse<?> deleteCity(String id);
}
