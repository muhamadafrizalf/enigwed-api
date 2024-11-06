package com.enigwed.dto.response;

import com.enigwed.entity.Product;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private WeddingOrganizerResponse weddingOrganizer;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private ImageResponse thumbnail;
    List<ImageResponse> images = new ArrayList<>();

    public static ProductResponse all(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(product.getWeddingOrganizer()));
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(product.getImages().get(0)));
            response.setImages(product.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setThumbnail(ImageResponse.noImage());
            response.getImages().add(ImageResponse.noImage());
        }
        return response;
    }

    public static ProductResponse information(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(product.getWeddingOrganizer()));
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(product.getImages().get(0)));
            response.setImages(product.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setThumbnail(ImageResponse.noImage());
            response.getImages().add(ImageResponse.noImage());
        }
        return response;
    }

    public static ProductResponse simple(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(product.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        return response;
    }


}
