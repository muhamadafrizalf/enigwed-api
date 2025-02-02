package com.enigwed.dto.response;

import com.enigwed.entity.UserCredential;
import com.enigwed.entity.WeddingOrganizer;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {
    private String token;
    private String role;
    private UserResponse user;

    public static LoginResponse noUser(UserCredential user, String token) {
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());
        return response;
    }

    public static LoginResponse withUser(UserCredential user, String token, WeddingOrganizer weddingOrganizer) {
        LoginResponse response = LoginResponse.noUser(user, token);
        response.setUser(UserResponse.from(weddingOrganizer));
        return response;
    }
}
