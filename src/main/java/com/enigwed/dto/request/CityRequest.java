package com.enigwed.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.enigwed.constant.Constraint.DESCRIPTION_MAX;
import static com.enigwed.constant.Constraint.NAME_BLANK;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CityRequest {

    private String id;

    @NotBlank(message = NAME_BLANK)
    private String name;

    @Size(max = 1000, message = DESCRIPTION_MAX)
    private String description;

    private MultipartFile thumbnail;
}
