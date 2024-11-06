package com.enigwed.dto.response;

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
    private ImageResponse thumbnail;
    private String name;
    private Double price;
    private String weddingOrganizerName;
    /* RATING[SOON] */
    private String description;
    private Integer orderCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private String provinceId;
    private String provinceName;
    private String regencyId;
    private String regencyName;
    private WeddingOrganizerResponse weddingOrganizer;
    private List<ImageResponse> images = new ArrayList<>();
    List<BonusDetailResponse> bonusDetails = new ArrayList<>();
    /* REVIEW[SOON] */

    public static WeddingPackageResponse all(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        response.setPrice(weddingPackage.getPrice());
        response.setCreatedAt(weddingPackage.getCreatedAt());
        response.setUpdatedAt(weddingPackage.getUpdatedAt());
        response.setDeletedAt(weddingPackage.getDeletedAt());
        response.setProvinceId(weddingPackage.getProvince().getId());
        response.setProvinceName(weddingPackage.getProvince().getName());
        response.setRegencyId(weddingPackage.getRegency().getId());
        response.setRegencyName(weddingPackage.getRegency().getName());
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(weddingPackage.getWeddingOrganizer()));
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
            response.setImages(weddingPackage.getImages().stream().map(ImageResponse::from).toList());
        } else {
            response.setThumbnail(ImageResponse.noImage());
            response.getImages().add(ImageResponse.noImage());
        }
        if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
            response.setBonusDetails(weddingPackage.getBonusDetails().stream().map(BonusDetailResponse::simple).toList());
        }
        return response;
    }

    public static WeddingPackageResponse information(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        response.setPrice(weddingPackage.getPrice());
        response.setProvinceName(weddingPackage.getProvince().getName());
        response.setRegencyName(weddingPackage.getRegency().getName());
        response.setWeddingOrganizer(WeddingOrganizerResponse.simple(weddingPackage.getWeddingOrganizer()));
        if (weddingPackage.getBonusDetails() != null && !weddingPackage.getBonusDetails().isEmpty()) {
            response.setBonusDetails(weddingPackage.getBonusDetails().stream().map(BonusDetailResponse::simple).toList());
        }
        return response;
    }

    public static WeddingPackageResponse simple(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        response.setName(weddingPackage.getName());
        response.setPrice(weddingPackage.getPrice());
        response.setWeddingOrganizerName(weddingPackage.getWeddingOrganizer().getName());
        /* RATING[SOON] */
        return response;
    }

    public static WeddingPackageResponse order(WeddingPackage weddingPackage) {
        WeddingPackageResponse response = new WeddingPackageResponse();
        response.setId(weddingPackage.getId());
        if (weddingPackage.getImages() != null && !weddingPackage.getImages().isEmpty()) {
            response.setThumbnail(ImageResponse.from(weddingPackage.getImages().get(0)));
        } else {
            response.setThumbnail(ImageResponse.noImage());
        }
        response.setName(weddingPackage.getName());
        response.setDescription(weddingPackage.getDescription());
        return response;
    }
}
