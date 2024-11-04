package com.enigwed.dto.response;

import com.enigwed.entity.WeddingPackage;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class WeddingPackageResponse {
    private String id;
    private String name;
    private String description;
    private double basePrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cityId;
    private String cityName;
    private WeddingOrganizerResponse weddingOrganizer;
    private ImageResponse thumbnail;
    private List<ImageResponse> images = new ArrayList<>();
    List<BonusDetailResponse> bonusDetails = new ArrayList<>();

    public static WeddingPackageResponse from(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        response.setBasePrice(weddingPackage.getBasePrice());
        response.setCreatedAt(weddingPackage.getCreatedAt());
        response.setUpdatedAt(weddingPackage.getUpdatedAt());
        response.setCityId(weddingPackage.getCity().getId());
        response.setCityName(weddingPackage.getCity().getName());
        response.setWeddingOrganizer(WeddingOrganizerResponse.from(weddingPackage.getWeddingOrganizer()));
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
            response.setImages(weddingPackage.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setThumbnail(ImageResponse.noImage());
            response.getImages().add(ImageResponse.noImage());
        }
        if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
            response.setBonusDetails(weddingPackage.getBonusDetails().stream().map(BonusDetailResponse::from).toList());
        }
        return response;
    }
}
