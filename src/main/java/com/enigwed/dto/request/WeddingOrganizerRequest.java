package com.enigwed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeddingOrganizerRequest {
    String id;
    private String name;
    private String phone;
    private String description;
    private String address;
    private String cityId;
}
