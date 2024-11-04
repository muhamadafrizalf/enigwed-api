package com.enigwed.dto.response;

import com.enigwed.entity.WeddingOrganizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
    private String name;
    private String email;
    private ImageResponse avatar;

    public static UserResponse fromUser(WeddingOrganizer weddingOrganizer) {
        UserResponse response = new UserResponse();
        response.setName(weddingOrganizer.getName());
        response.setEmail(weddingOrganizer.getUserCredential().getEmail());
        response.setAvatar(ImageResponse.from(weddingOrganizer.getAvatar()));
        return response;
    }
}