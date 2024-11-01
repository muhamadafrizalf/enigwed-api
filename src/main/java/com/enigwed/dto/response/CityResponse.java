package com.enigwed.dto.response;
import com.enigwed.entity.City;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ImageResponse thumbnail;

    public static CityResponse from(City city) {
        return CityResponse.builder()
                .id(city.getId())
                .name(city.getName())
                .description(city.getDescription())
                .createdAt(city.getCreatedAt())
                .updatedAt(city.getUpdatedAt())
                .thumbnail(ImageResponse.from(city.getThumbnail()))
                .build();
    }
}
