package com.enigwed.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {

    @NotBlank(message = ORDER_ID_BLANK)
    private String orderId;

    @Min(value = 0, message = RATING_INVALID)
    @Max(value = 5, message = RATING_INVALID)
    @NotNull(message = RATING_NULL)
    private Integer rating;

    @Size(max = 500, message = COMMENT_MAX_500)
    private String comment;

    private String customerName;

    private Boolean visiblePublic;
}
