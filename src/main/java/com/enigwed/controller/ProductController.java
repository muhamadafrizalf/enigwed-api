package com.enigwed.controller;

import com.enigwed.constant.SPathApi;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.ProductRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.security.JwtUtil;
import com.enigwed.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class ProductController {
    private final ProductService productService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "For customer to get product information by product_id (MOBILE)")
    @GetMapping(SPathApi.PUBLIC_PRODUCT_ID)
    public ResponseEntity<?> customerGetBonusPackageById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = productService.findProductById(id);
        return ResponseEntity.ok(response);
    }
    @Operation(
            summary = "For customer to search product own by one wedding organizer (Default pagination {page:1, size:8}) (MOBILE)",
            description = "wedding_organizer_id is mandatory and keyword is optional"
    )
    @GetMapping(SPathApi.PUBLIC_PRODUCT)
    public ResponseEntity<?> customerGetAllBonusPackages(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,
            @Parameter(description = "To filter by wedding_organizer_id", required = true)
            @RequestParam(required = false) String weddingOrganizerId,
            @Parameter(description = "Keyword can filter result by name, and description", required = false)
            @RequestParam(required = false) String keyword
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        ApiResponse<?> response;
        boolean isKeyword = keyword != null && !keyword.isEmpty();
        if (isKeyword) {
            response = productService.searchProductFromWeddingOrganizerId(weddingOrganizerId, keyword, pagingRequest);
        } else {
            response = productService.findAllProductsByWeddingOrganizerId(weddingOrganizerId, pagingRequest);
        }
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to get product information by product_id [WO] (WEB)",
            description = "Wedding organizer can only get their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @GetMapping(SPathApi.PROTECTED_PRODUCT_ID)
    public ResponseEntity<?> getOwnProductById(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.getOwnProductById(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to get all products own by wedding organizer (Default pagination {page:1, size:8}) [WO] (WEB)",
            description = "Wedding organizer can only retrieve their own products"
    )
    @PreAuthorize("hasRole('WO')")
    @GetMapping(SPathApi.PROTECTED_PRODUCT)
    public ResponseEntity<?> getOwnBonusPackages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.getOwnProducts(userInfo, pagingRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to create new product [WO] (WEB)"
    )
    @PreAuthorize("hasRole('WO')")
    @PostMapping(SPathApi.PROTECTED_PRODUCT)
    public ResponseEntity<?> createBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody ProductRequest productRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.createProduct(userInfo, productRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to update existing product [WO] (WEB)",
            description = "Wedding organizer can only update their own product"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @PutMapping(SPathApi.PROTECTED_PRODUCT)
    public ResponseEntity<?> updateBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @RequestBody ProductRequest productRequest
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.updateProduct(userInfo, productRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to delete product by product_id [WO] (WEB)",
            description = "Wedding organizer can only update their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(SPathApi.PROTECTED_PRODUCT_ID)
    public ResponseEntity<?> deleteBonusPackage(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.deleteProduct(userInfo, id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to add product image by product_id [WO] (WEB)",
            description = "Wedding organizer can only add image to their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(value = SPathApi.PROTECTED_PRODUCT_ID_IMAGE, consumes = {"multipart/form-data"})
    public ResponseEntity<?> addBonusPackageImages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @RequestPart(name = "image") MultipartFile image
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.addProductImage(userInfo, id, image);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "For wedding organizer to delete product image by product_id and image_id [WO] (WEB)",
            description = "WO can only delete image from their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(SPathApi.PROTECTED_PRODUCT_ID_IMAGE_ID)
    public ResponseEntity<?> deleteBonusPackageImages(
            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader,
            @PathVariable String id,
            @PathVariable(value = "image-id") String imageId
    ) {
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.deleteProductImage(userInfo, id, imageId);
        return ResponseEntity.ok(response);
    }

    // FOR DEVELOPMENT
    @Operation(
            summary = "For development to get all products in database (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @GetMapping(SPathApi.PUBLIC_PRODUCT + "/dev")
    public ResponseEntity<?> dev(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        ApiResponse<?> response = productService.findAllProducts(pagingRequest);
        return ResponseEntity.ok(response);
    }


}
