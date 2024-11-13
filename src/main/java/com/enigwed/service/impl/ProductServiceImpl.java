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
import com.enigwed.repository.spesification.SearchSpecifications;
import com.enigwed.service.ProductService;
import com.enigwed.service.ImageService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ImageService imageService;
    private final ValidationUtil validationUtil;
    private final AccessValidationUtil accessValidationUtil;

    private Product findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.PRODUCT_ID_IS_REQUIRED);
        return productRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.PRODUCT_NOT_FOUND(id)));
    }

    private ApiResponse<List<ProductResponse>> getListApiResponse(PagingRequest pagingRequest, List<Product> productList, Stream<ProductResponse> productResponseStream, WeddingOrganizer wo) throws ValidationException {
        /* VALIDATE PAGING REQUEST */
        if (pagingRequest != null) {
            validationUtil.validateAndThrow(pagingRequest);
        } else {
            pagingRequest = new PagingRequest(1, !productList.isEmpty() ? productList.size() : 1);
        }

        /* RETURN EMPTY LIST IF EMPTY */
        if (productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND(wo.getName()));

        /* MAP RESPONSE */
        List<ProductResponse> responses = productResponseStream.toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND(wo.getName(), productList.size()));
    }

    @Transactional(readOnly = true)
    @Override
    public Product loadProductById(String id) {
        try {
            /* FIND PRODUCT */
            // ErrorResponse //
            return findByIdOrThrow(id);
        } catch (ErrorResponse e) {
            log.error("Error while loading product by ID: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> customerFindAllProductsFromWeddingOrganizer(String weddingOrganizerId, PagingRequest pagingRequest, String keyword) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerById(weddingOrganizerId);

            /* FIND PRODUCTS */
            Sort sort = Sort.by(Sort.Order.asc("name"));
            Specification<Product> spec = SearchSpecifications.searchProduct(keyword);
            List<Product> productList = productRepository.findAll(spec, sort);
            productList = productList.stream().filter(product -> product.getWeddingOrganizer().getId().equals(weddingOrganizerId) && product.getDeletedAt() == null).toList();

            /* MAP RESPONSE */
            // ValidationException //
            return getListApiResponse(pagingRequest, productList, productList.stream().map(ProductResponse::card), wo);

        } catch (ValidationException e) {
            log.error("Validation error while loading products: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while loading products: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<ProductResponse> customerFindProductById(String id) {
        try {
            /* FIND PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(id);

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.information(product);
            return ApiResponse.success(response, SMessage.PRODUCT_FOUND(product.getId()));

        } catch (ErrorResponse e) {
            log.error("Error while loading product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> findOwnProducts(JwtClaim userInfo, PagingRequest pagingRequest, String keyword) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND PRODUCTS */
            Sort sort = Sort.by(Sort.Order.asc("name"));
            Specification<Product> spec = SearchSpecifications.searchProduct(keyword);
            List<Product> productList = productRepository.findAll(spec, sort);
            productList = productList.stream().filter(product -> product.getWeddingOrganizer().getId().equals(wo.getId()) && product.getDeletedAt() == null).toList();

            /* MAP RESPONSE */
            // ValidationException //
            return getListApiResponse(pagingRequest, productList, productList.stream().map(ProductResponse::card), wo);

        } catch (ValidationException e) {
            log.error("Validation error while loading own products: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while loading own products: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public ApiResponse<ProductResponse> findOwnProductById(JwtClaim userInfo, String id) {
        try {
            /* FIND PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, product.getWeddingOrganizer());

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_FOUND(id));

        } catch (AccessDeniedException e) {
            log.error("Access denied while loading own product: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.FETCHING_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while loading own product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> createProduct(JwtClaim userInfo, ProductRequest productRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer weddingOrganizer = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* CHECK IF WEDDING ORGANIZER HAS ANY BANK ACCOUNT */
            // ErrorResponse //
            if (weddingOrganizer.getBankAccounts() == null || weddingOrganizer.getBankAccounts().isEmpty())
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, SErrorMessage.BANK_ACCOUNT_EMPTY(weddingOrganizer.getName()));

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(productRequest);

            /* CREATE AND SAVE PRODUCT */
            Product product = Product.builder()
                    .name(productRequest.getName())
                    .description(productRequest.getDescription())
                    .price(productRequest.getPrice())
                    .weddingOrganizer(weddingOrganizer)
                    .build();
            product = productRepository.save(product);

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_CREATED(product.getId()));

        } catch (ValidationException e) {
            log.error("Validation error creating bonus: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error while creating product: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> updateProduct(JwtClaim userInfo, ProductRequest productRequest) {
        try {
            /* LOAD PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(productRequest.getId());

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, product.getWeddingOrganizer());


            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(productRequest);

            /* UPDATE AND SAVE PRODUCT */
            product.setName(productRequest.getName());
            product.setDescription(productRequest.getDescription());
            product.setPrice(productRequest.getPrice());
            product = productRepository.save(product);

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_UPDATED(product.getId()));

        } catch (AccessDeniedException e) {
          log.error("Access denied while updating product: {}", e.getMessage());
          throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ValidationException e) {
            log.error("Validation error while updating product: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while updating product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteProduct(JwtClaim userInfo, String id) {
        try {
            /* LOAD PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, product.getWeddingOrganizer());

            /* SET DELETED AT AND SAVE PRODUCT */
            product.setDeletedAt(LocalDateTime.now());
            productRepository.save(product);

            /* MAP RESPONSE */
            return ApiResponse.success(SMessage.PRODUCT_DELETED(product.getId()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting product: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting product: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> addProductImage(JwtClaim userInfo, String id, MultipartFile image) {
        try {
            /* LOAD PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, product.getWeddingOrganizer());

            /* SAVE PRODUCT IMAGE */
            // ErrorResponse //
            Image addedImage = imageService.createImage(image);
            if (product.getImages() == null) {
                product.setImages(new ArrayList<>());
            }
            product.getImages().add(addedImage);
            product = productRepository.save(product);

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_IMAGE_ADDED(product.getName()));

        } catch (AccessDeniedException e) {
            log.error("Access denied while adding product image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while adding product image: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ProductResponse> deleteProductImage(JwtClaim userInfo, String id, String imageId) {
        try {
            /* LOAD PRODUCT */
            // ErrorResponse //
            Product product = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, product.getWeddingOrganizer());

            /* DELETE PRODUCT IMAGE */
            // ErrorResponse //
            imageService.deleteImage(imageId);
            List<Image> images = product.getImages();
            if (images != null) {
                images.removeIf(image -> image.getId().equals(imageId));
            }
            product = productRepository.save(product);

            /* MAP RESPONSE */
            ProductResponse response = ProductResponse.all(product);
            return ApiResponse.success(response, SMessage.PRODUCT_IMAGE_DELETED(product.getName(), imageId));

        } catch (AccessDeniedException e) {
            log.error("Access denied while deleting product image: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.DELETE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while deleting product image: {}", e.getError());
            throw e;
        }
    }

    // FOR DEVELOPMENT //

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<ProductResponse>> findAllProducts(PagingRequest pagingRequest, String keyword) {
        try {
            /* FIND PRODUCTS */
            Sort sort = Sort.by(Sort.Order.asc("name"));
            Specification<Product> spec = SearchSpecifications.searchProduct(keyword);
            List<Product> productList = productRepository.findAll(spec, sort);

            /* VALIDATE PAGING REQUEST */
            if (pagingRequest != null) {
                validationUtil.validateAndThrow(pagingRequest);
            } else {
                pagingRequest = new PagingRequest(1, productList.size());
            }

            /* RETURN EMPTY LIST IF EMPTY */
            if (productList.isEmpty()) return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_PRODUCT_FOUND);

            /* MAP RESPONSE */
            List<ProductResponse> responses = productList.stream().map(ProductResponse::all).toList();
            return ApiResponse.success(responses, pagingRequest, SMessage.PRODUCTS_FOUND(productList.size()));

        } catch (ValidationException e) {
            log.error("Validation error while loading all products: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, e.getErrors().get(0));
        }
    }

}
