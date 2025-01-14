package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.ProductRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.ProductResponse;
import com.enigwed.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    // Use in other service
    Product loadProductById(String id);
    // Customer
    ApiResponse<List<ProductResponse>> customerFindAllProductsFromWeddingOrganizer(String weddingOrganizerId, PagingRequest pagingRequest, String keyword);
    ApiResponse<ProductResponse> customerFindProductById(String id);
    // Wedding organizer
    ApiResponse<List<ProductResponse>> findOwnProducts(JwtClaim userInfo, PagingRequest pagingRequest, String keyword);
    ApiResponse<ProductResponse> findOwnProductById(JwtClaim userInfo, String id);
    ApiResponse<ProductResponse> createProduct(JwtClaim userInfo, ProductRequest productRequest);
    ApiResponse<ProductResponse> updateProduct(JwtClaim userInfo, ProductRequest productRequest);
    ApiResponse<?> deleteProduct(JwtClaim userInfo, String id);
    ApiResponse<ProductResponse> addProductImage(JwtClaim userInfo, String id, MultipartFile image);
    ApiResponse<ProductResponse> deleteProductImage(JwtClaim userInfo, String id, String imageId);
    // FOR DEVELOPMENT DONT USE
    ApiResponse<List<ProductResponse>> findAllProducts(PagingRequest pagingRequest, String keyword);
}
