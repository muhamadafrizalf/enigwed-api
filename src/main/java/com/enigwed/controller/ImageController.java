package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {
    private final ImageService imageService;

    // For Development Use
    @Operation(
            summary = "For development to get image resource (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @GetMapping(SPathApi.PUBLIC_IMAGE_RESOURCE_ID)
    public ResponseEntity<?> getRawImageById(@PathVariable String id) {
        Resource image = imageService.loadImageResourceById(id);
        return ResponseEntity.ok(image);
    }

    // For Development Use
    @Operation(
            summary = "For development to get image response (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @GetMapping(SPathApi.PUBLIC_IMAGE_ID)
    public ResponseEntity<?> getImageById(@PathVariable String id) {
        ApiResponse<?> response = imageService.findByIdResponse(id);
        return ResponseEntity.ok(response);
    }
}
