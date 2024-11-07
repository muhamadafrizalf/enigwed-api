package com.enigwed.service;

import com.enigwed.dto.request.ReviewRequest;
import com.enigwed.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    // FOR DEVELOPMENT USE
    ReviewResponse createReview(ReviewRequest reviewRequest);
    List<ReviewResponse> getAllReviews();
}
