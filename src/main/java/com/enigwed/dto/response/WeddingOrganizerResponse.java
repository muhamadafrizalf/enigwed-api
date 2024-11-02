package com.enigwed.dto.response;

import com.enigwed.entity.WeddingOrganizer;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class WeddingOrganizerResponse {
    private String id;
    private String name;
    private String npwp;
    private String nib;
    private String phone;
    private String description;
    private String address;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ImageResponse avatar;
    private CityResponse city;
    private boolean active;

    public static WeddingOrganizerResponse from(WeddingOrganizer weddingOrganizer) {
        WeddingOrganizerResponse response = new WeddingOrganizerResponse();
        response.setId(weddingOrganizer.getId());
        response.setName(weddingOrganizer.getName());
        response.setNpwp(weddingOrganizer.getNpwp());
        response.setNib(weddingOrganizer.getNib());
        response.setPhone(weddingOrganizer.getPhone());
        response.setDescription(weddingOrganizer.getDescription());
        response.setAddress(weddingOrganizer.getAddress());
        response.setCreatedAt(weddingOrganizer.getCreatedAt());
        response.setUpdatedAt(weddingOrganizer.getUpdatedAt());
        response.setAvatar(ImageResponse.from(weddingOrganizer.getAvatar()));
        response.setCity(CityResponse.from(weddingOrganizer.getCity()));
        response.setActive(weddingOrganizer.getUserCredential().isActive());
        return response;
    }
}
