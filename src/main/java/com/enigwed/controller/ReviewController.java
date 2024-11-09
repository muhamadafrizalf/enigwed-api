package com.enigwed.controller;

import com.enigwed.constant.PathApi;
import com.enigwed.dto.request.ReviewRequest;
import com.enigwed.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    // FOR DEVELOPMENT
    @Operation(
            summary = "For development to get all review in database (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @GetMapping("/api/public/reviews")
    public ResponseEntity<?> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    // FOR DEVELOPMENT
    @Operation(
            summary = "For development to create new review (FOR DEVELOPMENT ONLY, DON'T USE)"
    )
    @PostMapping("/api/public/reviews")
    public ResponseEntity<?> createReview(@RequestBody ReviewRequest reviewRequest) {
        return ResponseEntity.ok(reviewService.createReview(reviewRequest));
    }
}
