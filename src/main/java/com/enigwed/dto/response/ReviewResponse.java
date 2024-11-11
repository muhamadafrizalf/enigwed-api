package com.enigwed.dto.response;

import com.enigwed.entity.Review;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReviewResponse {
    private String id;
    private String orderId;
    private String weddingOrganizerId;
    private String weddingOrganizerName;
    private String weddingPackageId;
    private String weddingPackageName;
    private Double rating;
    private String customerName;
    private String comment;

    public static ReviewResponse from(Review review) {
        ReviewResponse response = new ReviewResponse();
        response.setId(review.getId());
        response.setOrderId(review.getOrder().getId());
        response.setWeddingOrganizerId(review.getWeddingOrganizer().getId());
        response.setWeddingOrganizerName(review.getWeddingOrganizer().getName());
        response.setWeddingPackageId(review.getWeddingPackage().getId());
        response.setWeddingPackageName(review.getWeddingPackage().getName());
        response.setRating(review.getRating());
        response.setCustomerName(review.getCustomerName());
        if (review.getComment() != null && !review.getComment().isEmpty()) {
            response.setComment(review.getComment());
        } else {
            response.setComment("");
        }
        return response;
    }
}
