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
@NoArgsConstructor
public class CityResponse {
    private String id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ImageResponse thumbnail;

    public static CityResponse from(City city) {
        CityResponse response = new CityResponse();
        response.setId(city.getId());
        response.setName(city.getName());
        response.setDescription(city.getDescription());
        response.setCreatedAt(city.getCreatedAt());
        response.setUpdatedAt(city.getUpdatedAt());
        response.setThumbnail(ImageResponse.from(city.getThumbnail()));
        return response;
    }
}
