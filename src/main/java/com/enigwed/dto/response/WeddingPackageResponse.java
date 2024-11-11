package com.enigwed.dto.response;

import com.enigwed.entity.Review;
import com.enigwed.entity.WeddingPackage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeddingPackageResponse {
    private String id;
    private String name;
    private String description;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    private String provinceId;
    private String provinceName;
    private String regencyId;
    private String regencyName;
    private Integer orderCount;
    private Double price;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private ImageResponse thumbnail;
    private WeddingOrganizerResponse weddingOrganizer;
    private List<ImageResponse> images;
    private List<BonusDetailResponse> bonusDetails;
    private List<ReviewResponse> reviews;

    public static WeddingPackageResponse card(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        response.setPrice(weddingPackage.getPrice());
        response.setOrderCount(weddingPackage.getOrderCount());
        response.setWeddingOrganizerId(weddingPackage.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(weddingPackage.getWeddingOrganizer().getName());
        response.setProvinceId(weddingPackage.getProvince().getId());
        response.setProvinceName(weddingPackage.getProvince().getName());
        response.setRegencyId(weddingPackage.getRegency().getId());
        response.setRegencyName(weddingPackage.getRegency().getName());
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(weddingPackage.getWeddingOrganizer()));
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        if (weddingPackage.getReviews() != null && !weddingPackage.getReviews().isEmpty()) {
            response.setRating(weddingPackage.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0));
        } else {
            response.setRating(0.0);
        }
        return response;
    }

    public static WeddingPackageResponse information(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = WeddingPackageResponse.card(weddingPackage);
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setImages(weddingPackage.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setImages(List.of(ImageResponse.noImage()));
        }
        if (weddingPackage.getReviews() != null && !weddingPackage.getReviews().isEmpty()) {
            response.setReviews(weddingPackage.getReviews().stream().filter(Review::isVisiblePublic).map(ReviewResponse::from).toList());
        } else {
            response.setReviews(new ArrayList<>());
        }
        if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
            response.setBonusDetails(weddingPackage.getBonusDetails().stream().filter(bonusDetail -> bonusDetail.getProduct().getDeletedAt() == null).map(BonusDetailResponse::from).toList());
        } else {
            response.setBonusDetails(new ArrayList<>());
        }
        return response;
    }

    public static WeddingPackageResponse all(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = WeddingPackageResponse.information(weddingPackage);
        response.setCreatedAt(weddingPackage.getCreatedAt());
        response.setUpdatedAt(weddingPackage.getUpdatedAt());
        response.setDeletedAt(weddingPackage.getDeletedAt());
        if (weddingPackage.getReviews() != null && !weddingPackage.getReviews().isEmpty()) {
            response.setReviews(weddingPackage.getReviews().stream().map(ReviewResponse::from).toList());
        } else {
            response.setReviews(new ArrayList<>());
        }
        return response;
    }
}
