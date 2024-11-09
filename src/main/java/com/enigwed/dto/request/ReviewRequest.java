package com.enigwed.dto.request;

import com.enigwed.constant.SConstraint;
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

    private String customerName;

    @Size(max = 500, message = COMMENT_MAX_500)
    private String comment;

    private Boolean visiblePublic;
}
