package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.service.ImageService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ImageController {
    private final ImageService imageService;

    @GetMapping(PathApi.PUBLIC_IMAGE_ID)
    public ResponseEntity<?> getImageById(@PathVariable String id) {
        Resource image = imageService.findById(id);
        return ResponseEntity.ok(image);
    }
}
