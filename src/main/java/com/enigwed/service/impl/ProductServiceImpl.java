package com.enigwed.service.impl;

import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.ProductRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.ProductResponse;
import com.enigwed.entity.Product;
import com.enigwed.entity.Image;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.ProductRepository;
import com.enigwed.service.ProductService;
import com.enigwed.service.ImageService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;

    private Product findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.ID_IS_REQUIRED);
        return productRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.BONUS_PACKAGE_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, Product product) throws AccessDeniedException {
        String userCredentialId = product.getWeddingOrganizer().getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

    @Transactional(readOnly = true)
    @Override
    public Product loadProductById(String id) {
        try {
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error during loading product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<ProductResponse> findProductById(String id) {
        try {
            // ErrorResponse
            Product product = findByIdOrThrow(id);
            ProductResponse response = ProductResponse.information(product);
            return ApiResponse.success(response, SMessage.PRODUCT_FOUND);
        } catch (ErrorResponse e) {
            log.error("Error during loading product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> findAllProductsByWeddingOrganizerId(String weddingOrganizerId, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        List<Product> productList = productRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(weddingOrganizerId);
        if (productList == null || productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND);
        List<ProductResponse> responses = productList.stream().map(ProductResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> searchProductFromWeddingOrganizerId(String weddingOrganizerId, String keyword, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        List<Product> productList = productRepository.findByWeddingOrganizerIdAndKeyword(weddingOrganizerId, keyword);
        if (productList == null || productList.isEmpty()) return ApiResponse.success(new ArrayList<>(),pagingRequest, SMessage.NO_PRODUCT_FOUND);
        List<ProductResponse> responses = productList.stream().map(ProductResponse::simple).toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND);
    }

    @Override
    public ApiResponse<ProductResponse> getOwnProductById(JwtClaim userInfo, String id) {
        try {
            Product product = findByIdOrThrow(id);

            validateUserAccess(userInfo, product);

            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_FOUND);
        } catch (AccessDeniedException e) {
            log.error("Access denied during loading own product: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during loading own product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> getOwnProducts(JwtClaim userInfo, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
        List<Product> productList = productRepository.findByWeddingOrganizerIdAndDeletedAtIsNull(wo.getId());
        if (productList == null || productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND);
        List<ProductResponse> responses = productList.stream().map(ProductResponse::all).toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> createProduct(JwtClaim userInfo, ProductRequest productRequest) {
        try {
            // ErrorResponse
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            // ValidationException
            validationUtil.validateAndThrow(productRequest);

            Product product = Product.builder()
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .weddingOrganizer(weddingOrganizer)
                    .build();

            product = productRepository.save(product);

            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_CREATED);

        } catch (ValidationException e) {
            log.error("Validation error creating bonus: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error during creating product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> updateProduct(JwtClaim userInfo, ProductRequest productRequest) {
        try {
            // ErrorResponse
            Product product = findByIdOrThrow(productRequest.getId());

            // AccessDeniedException
            validateUserAccess(userInfo, product);

            // ValidationException
            validationUtil.validateAndThrow(productRequest);


            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());

            product = productRepository.save(product);

            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_UPDATED);

        } catch (AccessDeniedException e) {
          log.error("Access denied during updating product: {}", e.getMessage());
          throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error during updating product: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error during updating product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteProduct(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            Product product = findByIdOrThrow(id);

            // AccessDeniedException
            validateUserAccess(userInfo, product);

            product.setDeletedAt(LocalDateTime.now());

            productRepository.save(product);

            return ApiResponse.success(SMessage.PRODUCT_DELETED);

        } catch (AccessDeniedException e) {
            log.error("Access denied during deletion product: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> addProductImage(JwtClaim userInfo, String id, MultipartFile image) {
        try {
            // ErrorResponse
            Product product = findByIdOrThrow(id);

            // AccessDeniedException
            validateUserAccess(userInfo, product);

            // ErrorResponse
            Image addedImage = imageService.createImage(image);
            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            }
            product.getImages().add(addedImage);

            product = productRepository.save(product);

            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_UPDATED);

        } catch (AccessDeniedException e) {
            log.error("Access denied during adding product image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during adding product image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> deleteProductImage(JwtClaim userInfo, String id, String imageId) {
        try {
            // ErrorResponse
            Product product = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, product);
            // ErrorResponse
            imageService.deleteImage(imageId);
            List<Image> images = product.getImages();
            if (images != null) {
                images.removeIf(image -> image.getId().equals(imageId));
            }
            product = productRepository.save(product);
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_UPDATED);
        } catch (AccessDeniedException e) {
            log.error("Access denied during deleting product image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during deleting product image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> findAllProducts(PagingRequest pagingRequest) {
        try {
            // ValidationException
            validationUtil.validateAndThrow(pagingRequest);
            List<Product> productList = productRepository.findByDeletedAtIsNull();
            if (productList == null || productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND);
            List<ProductResponse> responses = productList.stream().map(ProductResponse::all).toList();
            return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND);
        } catch (ValidationException e) {
            log.error("Validation error while loading products: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error error while loading products: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> searchProducts(String keyword, PagingRequest pagingRequest) {
        validationUtil.validateAndThrow(pagingRequest);
        List<Product> productList = productRepository.searchBonusPackage(keyword);
        if (productList == null || productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND);
        List<ProductResponse> responses = productList.stream().map(ProductResponse::all).toList();
        return ApiResponse.success(responses, SMessage.PRODUCTS_FOUND);
    }
}
