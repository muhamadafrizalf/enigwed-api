package com.enigwed.dto.response;

import com.enigwed.entity.Review;
import com.enigwed.entity.WeddingPackage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeddingPackageResponse {
    private String id;
    private ImageResponse thumbnail;
    private String name;
    private String description;
    private Double price;
    private Integer orderCount;
    private Double rating;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    List<ReviewResponse> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String provinceId;
    private String provinceName;
    private String regencyId;
    private String regencyName;
    private WeddingOrganizerResponse weddingOrganizer;
    private List<ImageResponse> images;
    List<BonusDetailResponse> bonusDetails;

    public static WeddingPackageResponse card(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        response.setPrice(weddingPackage.getPrice());
        response.setOrderCount(weddingPackage.getOrderCount());
        if (weddingPackage.getReviews() != null && !weddingPackage.getReviews().isEmpty()) {
            response.setRating(weddingPackage.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0));
        }
        response.setWeddingOrganizerId(weddingPackage.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(weddingPackage.getWeddingOrganizer().getName());
        response.setRegencyName(weddingPackage.getRegency().getName());
        return response;
    }



    public static WeddingPackageResponse information(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = WeddingPackageResponse.card(weddingPackage);
        response.setProvinceName(weddingPackage.getProvince().getName());
        response.setRegencyName(weddingPackage.getRegency().getName());
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(weddingPackage.getWeddingOrganizer()));
        if (weddingPackage.getReviews() != null && !weddingPackage.getReviews().isEmpty()) {
            response.setReviews(weddingPackage.getReviews().stream().filter(Review::isVisiblePublic).map(ReviewResponse::simple).toList());
        }
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setImages(weddingPackage.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setImages(List.of(ImageResponse.noImage()));
        }
        if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
            response.setBonusDetails(weddingPackage.getBonusDetails().stream().map(BonusDetailResponse::simple).toList());
        }
        return response;
    }

    public static WeddingPackageResponse all(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = WeddingPackageResponse.information(weddingPackage);
        response.setCreatedAt(weddingPackage.getCreatedAt());
        response.setUpdatedAt(weddingPackage.getUpdatedAt());
        response.setDeletedAt(weddingPackage.getDeletedAt());
        response.setProvinceId(weddingPackage.getProvince().getId());
        response.setRegencyId(weddingPackage.getRegency().getId());
        return response;
    }
}
