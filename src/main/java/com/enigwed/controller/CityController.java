package com.enigwed.controller;

import com.enigwed.constant.Message;
import com.enigwed.constant.PathApi;
import com.enigwed.dto.request.CityRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.service.CityService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CityController {
    private final CityService cityService;
    private final ObjectMapper objectMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = PathApi.PROTECTED_CITY, consumes = {"multipart/form-data"})
    public ResponseEntity<?> createCity(
            @RequestPart(name = "city") String jsonCity,
            @RequestPart(name = "thumbnail", required = true) MultipartFile thumbnail
    ) {
        try {
            CityRequest request = objectMapper.readValue(jsonCity, new TypeReference<CityRequest>() {});
            if (thumbnail != null && !thumbnail.isEmpty()) {
                request.setThumbnail(thumbnail);
            }

            ApiResponse<?> response = cityService.createCity(request);

            return ResponseEntity.ok(response);
        } catch (ErrorResponse e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.CREATE_FAILED, e.getMessage());
        }
    }

    @GetMapping(PathApi.PUBLIC_CITY_ID)
    public ResponseEntity<?> getCityById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = cityService.findCityById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping(PathApi.PUBLIC_CITY)
    public ResponseEntity<?> getCities(
            @RequestParam(required = false) String name
    ) {
        ApiResponse<?> response;
        if (name != null && !name.isEmpty()) {
            response = cityService.findCityByName(name);
        } else {
            response = cityService.findAllCity();
        }
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = PathApi.PROTECTED_CITY, consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateCity(
            @RequestPart(name = "city") String jsonCity,
            @RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail
    ) {
        try {
            CityRequest request = objectMapper.readValue(jsonCity, new TypeReference<CityRequest>() {});
            if (thumbnail != null && !thumbnail.isEmpty()) {
                request.setThumbnail(thumbnail);
            }
            ApiResponse<?> response = cityService.updateCity(request);

            return ResponseEntity.ok(response);
        } catch (ErrorResponse e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, Message.CREATE_FAILED, e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping(PathApi.PROTECTED_CITY_ID)
    public ResponseEntity<?> deleteCityById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = cityService.deleteCity(id);

        return ResponseEntity.ok(response);
    }

}
