package com.enigwed.dto.response;

import com.enigwed.entity.BonusPackage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BonusPackageResponse {
    private String id;
    private String name;
    private String description;
    private double price;
    private int minQuantity;
    private int maxQuantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    private ImageResponse thumbnail;
    List<ImageResponse> images;

    public static BonusPackageResponse from(BonusPackage bonusPackage) {
        BonusPackageResponse response = new BonusPackageResponse();
        response.setId(bonusPackage.getId());
        response.setName(bonusPackage.getName());
        response.setDescription(bonusPackage.getDescription());
        response.setPrice(bonusPackage.getPrice());
        response.setMinQuantity(bonusPackage.getMinQuantity());
        response.setMaxQuantity(bonusPackage.getMaxQuantity());
        response.setCreatedAt(bonusPackage.getCreatedAt());
        response.setUpdatedAt(bonusPackage.getUpdatedAt());
        response.setWeddingOrganizerId(bonusPackage.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(bonusPackage.getWeddingOrganizer().getName());
        if (bonusPackage.getImages() != null && !bonusPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(bonusPackage.getImages().get(0)));
            response.setImages(bonusPackage.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setThumbnail(ImageResponse.noImage());
            response.setImages(new ArrayList<>());
        }
        return response;
    }
}