package com.enigwed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    private String name;
    private String description;
    private String address;
    private String npwp;
    private String nib;
    private String cityId;
    private String phone;
    private String email;
    private String password;
    private String confirmPassword;
}
