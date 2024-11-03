package com.enigwed.dto.response;

import com.enigwed.entity.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ImageResponse {
    private String id;
    private String name;
    private String url;

    public static ImageResponse from(Image image) {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setId(image.getId());
        if (image.getPath() != null) {
            imageResponse.setName(image.getName());
            imageResponse.setUrl("images/" + image.getName());
        } else {
            imageResponse.setName("No_Image_Available.jpg");
            imageResponse.setUrl("images/No_Image_Available.jpg");
        }
        return imageResponse;
    }
    public static ImageResponse noImage() {
        ImageResponse imageResponse = new ImageResponse();
        imageResponse.setName("No_Image_Available.jpg");
        imageResponse.setUrl("images/No_Image_Available.jpg");
        return imageResponse;
    }
}
