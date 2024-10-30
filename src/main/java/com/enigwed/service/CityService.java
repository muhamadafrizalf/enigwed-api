package com.enigwed.service;

import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.entity.City;

import java.util.List;

public interface CityService {
    ApiResponse<City> create(CityRequest cityRequest);
    ApiResponse<City> findById(String id);
    ApiResponse<City> findByName(String name);
    ApiResponse<List<City>> findAll();
    ApiResponse<City> update(CityRequest cityRequest);
    ApiResponse<?> deleteById(String id);
}
