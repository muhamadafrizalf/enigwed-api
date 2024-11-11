package com.enigwed.dto.response;

import com.enigwed.constant.EUserStatus;
import com.enigwed.entity.Review;
import com.enigwed.entity.WeddingOrganizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WeddingOrganizerResponse {
    private String id;
    private ImageResponse avatar;
    private String name;
    private String npwp;
    private String nib;
    private String phone;
    private String email;
    private String description;
    private String provinceId;
    private String provinceName;
    private String regencyId;
    private String regencyName;
    private String districtId;
    private String districtName;
    private String address;
    private Integer weddingPackageCount;
    private Double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private EUserStatus status;
    private LocalDateTime activeUntil;
    private List<BankAccountResponse> bankAccounts;

    public static WeddingOrganizerResponse card(WeddingOrganizer weddingOrganizer) {
        WeddingOrganizerResponse response = new WeddingOrganizerResponse();
        response.setId(weddingOrganizer.getId());
        response.setAvatar(ImageResponse.from(weddingOrganizer.getAvatar()));
        response.setName(weddingOrganizer.getName());
        response.setPhone(weddingOrganizer.getPhone());
        response.setDescription(weddingOrganizer.getDescription());
        response.setProvinceId(weddingOrganizer.getProvince().getId());
        response.setRegencyId(weddingOrganizer.getRegency().getId());
        response.setDistrictId(weddingOrganizer.getDistrict().getId());
        response.setProvinceName(weddingOrganizer.getProvince().getName());
        response.setRegencyName(weddingOrganizer.getRegency().getName());
        response.setDistrictName(weddingOrganizer.getDistrict().getName());
        response.setWeddingPackageCount(
                (int) weddingOrganizer.getWeddingPackages().stream()
                        .filter(weddingPackage -> weddingPackage.getDeletedAt() == null)
                        .count()
        );
        if (weddingOrganizer.getReviews() != null && !weddingOrganizer.getReviews().isEmpty()) {
            response.setRating(weddingOrganizer.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0));
        }
        return response;
    }

    public static WeddingOrganizerResponse cardAdmin(WeddingOrganizer weddingOrganizer) {
        WeddingOrganizerResponse response = WeddingOrganizerResponse.card(weddingOrganizer);
        response.setActiveUntil(weddingOrganizer.getUserCredential().getActiveUntil());
        if (weddingOrganizer.getDeletedAt() != null) {
            response.setStatus(EUserStatus.DELETED);
        } else if (weddingOrganizer.getUserCredential().isActive()) {
            response.setStatus(EUserStatus.ACTIVE);
        } else {
            response.setStatus(EUserStatus.INACTIVE);
        }
        return response;
    }

    public static WeddingOrganizerResponse information(WeddingOrganizer weddingOrganizer) {
        WeddingOrganizerResponse response = WeddingOrganizerResponse.card(weddingOrganizer);
        response.setPhone(weddingOrganizer.getPhone());
        response.setAddress(weddingOrganizer.getAddress());
        if (weddingOrganizer.getBankAccounts() != null && !weddingOrganizer.getBankAccounts().isEmpty()) {
            response.setBankAccounts(weddingOrganizer.getBankAccounts().stream().filter(bankAccount -> bankAccount.getDeletedAt() == null).map(BankAccountResponse::all).toList());
        } else {
            response.setBankAccounts(new ArrayList<>());
        }
        return response;
    }

    public static WeddingOrganizerResponse all(WeddingOrganizer weddingOrganizer) {
        WeddingOrganizerResponse response = WeddingOrganizerResponse.information(weddingOrganizer);
        response.setNpwp(weddingOrganizer.getNpwp());
        response.setNib(weddingOrganizer.getNib());
        response.setEmail(weddingOrganizer.getUserCredential().getEmail());
        response.setCreatedAt(weddingOrganizer.getCreatedAt());
        response.setUpdatedAt(weddingOrganizer.getUpdatedAt());
        response.setDeletedAt(weddingOrganizer.getDeletedAt());
        response.setActiveUntil(weddingOrganizer.getUserCredential().getActiveUntil());
        if (weddingOrganizer.getDeletedAt() != null) {
            response.setStatus(EUserStatus.DELETED);
        } else if (weddingOrganizer.getUserCredential().isActive()) {
            response.setStatus(EUserStatus.ACTIVE);
        } else {
            response.setStatus(EUserStatus.INACTIVE);
        }
        return response;
    }
}
