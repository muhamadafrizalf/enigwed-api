package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityRequest {

    private String id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private MultipartFile thumbnail;
}
