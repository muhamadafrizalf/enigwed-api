package com.enigwed.service;

import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.CityResponse;
import com.enigwed.entity.City;

import java.util.List;

public interface CityService {
    City loadCityById(String id);

    ApiResponse<CityResponse> create(CityRequest cityRequest);
    ApiResponse<CityResponse> findById(String id);
    ApiResponse<CityResponse> findByName(String name);
    ApiResponse<List<CityResponse>> findAll();
    ApiResponse<CityResponse> update(CityRequest cityRequest);
    ApiResponse<?> deleteById(String id);
}
