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
    private Double price;
    private String description;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private ImageResponse thumbnail;
    private WeddingOrganizerResponse weddingOrganizer;
    List<ImageResponse> images = new ArrayList<>();

    public static ProductResponse card(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setDescription(product.getDescription());
        response.setWeddingOrganizerId(product.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(product.getWeddingOrganizer().getName());
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(product.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        return response;
    }

    public static ProductResponse information(Product product) {
        ProductResponse response = ProductResponse.card(product);
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(product.getWeddingOrganizer()));
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            response.setImages(product.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setImages(List.of(ImageResponse.noImage()));
        }
        return response;
    }

    public static ProductResponse all(Product product) {
        ProductResponse response = ProductResponse.information(product);
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());
        return response;
    }

}
