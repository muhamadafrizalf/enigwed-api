package com.enigwed.controller;

import com.enigwed.constant.PathApi;
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

    @Operation(summary = "To get product by product_id (MOBILE)")
    @GetMapping(PathApi.PUBLIC_PRODUCT_ID)
    public ResponseEntity<?> customerGetBonusPackageById(
            @PathVariable String id
    ) {
        ApiResponse<?> response = productService.findProductById(id);
        return ResponseEntity.ok(response);
    }

    /* IMPLEMENT PAGING [SOON] */
    @Operation(
            summary = "To search product own by one wedding organizer (MOBILE)",
            description = "wedding_organizer_id is mandatory and keyword is optional (USE PAGING[SOON])"
    )
    @GetMapping(PathApi.PUBLIC_PRODUCT)
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

    @Operation(summary = "To get own product by product_id [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_PRODUCT_ID)
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
            summary = "To get all products own by wedding organizer [WO] (WEB)",
            description = "WO can only receive their own products"
    )
    @PreAuthorize("hasRole('WO')")
    @GetMapping(PathApi.PROTECTED_PRODUCT)
    public ResponseEntity<?> getOwnBonusPackages(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size,

            @Parameter(description = "Http header token bearer", example = "Bearer string_token", required = true)
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        JwtClaim userInfo = jwtUtil.getUserInfoByHeader(authHeader);
        ApiResponse<?> response = productService.getOwnProducts(userInfo, pagingRequest);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "To create new product [WO] (WEB)")
    @PreAuthorize("hasRole('WO')")
    @PostMapping(PathApi.PROTECTED_PRODUCT)
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
            summary = "To update existing product [WO] (WEB)",
            description = "WO can only update their own product"
    )
    @PreAuthorize("hasAnyRole('WO')")
    @PutMapping(PathApi.PROTECTED_PRODUCT)
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
            summary = "To delete product by product_id [WO] (WEB)",
            description = "WO can only update their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(PathApi.PROTECTED_PRODUCT_ID)
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
            summary = "To delete product by product_id [WO] (WEB)",
            description = "WO can only add image to their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @PutMapping(value = PathApi.PROTECTED_PRODUCT_ID_IMAGE, consumes = {"multipart/form-data"})
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
            summary = "To delete product by product_id [WO] (WEB)",
            description = "WO can only delete image from their own product"
    )
    @PreAuthorize("hasRole('WO')")
    @DeleteMapping(PathApi.PROTECTED_PRODUCT_ID_IMAGE_ID)
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
            summary = "Get all products in database (For development only, don't use)"
    )
    @GetMapping(PathApi.PUBLIC_PRODUCT + "/dev")
    public ResponseEntity<?> dev(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "8") int size
    ) {
        PagingRequest pagingRequest = new PagingRequest(page, size);
        ApiResponse<?> response = productService.findAllProducts(pagingRequest);
        return ResponseEntity.ok(response);
    }


}
