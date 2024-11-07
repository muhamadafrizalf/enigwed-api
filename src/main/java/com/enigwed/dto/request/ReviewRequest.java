package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewRequest {

    private String orderId;

    private String customerName;

    @NotNull(message = Constraint.RATING_NULL)
    @Min(value = 0, message = Constraint.INVALID_RATING)
    @Max(value = 5, message = Constraint.INVALID_RATING)
    private Integer rating;

    @Size(max = 500, message = Constraint.COMMENT_MAX_500)
    private String comment;
}
