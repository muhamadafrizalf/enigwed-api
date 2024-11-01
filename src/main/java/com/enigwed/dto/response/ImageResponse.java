package com.enigwed.dto.response;

import com.enigwed.entity.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponse {
    private String id;
    private String name;
    private String url;

    public static ImageResponse from(Image image) {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setId(image.getId());
        if (image.getName() != null) {
            imageResponse.setName(image.getName());
            imageResponse.setUrl("images/" + image.getName());
        }
        return imageResponse;
    }
}
