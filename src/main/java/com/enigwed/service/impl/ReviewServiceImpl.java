package com.enigwed.service.impl;

import com.enigwed.dto.request.ReviewRequest;
import com.enigwed.dto.response.ReviewResponse;
import com.enigwed.entity.Order;
import com.enigwed.entity.Review;
import com.enigwed.repository.ReviewRepository;
import com.enigwed.service.OrderService;
import com.enigwed.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final OrderService orderService;

    @Override
    public ReviewResponse createReview(ReviewRequest reviewRequest) {
        Order order = orderService.loadOrderById(reviewRequest.getOrderId());
        Review review = Review.builder()
                .order(order)
                .weddingOrganizer(order.getWeddingOrganizer())
                .weddingPackage(order.getWeddingPackage())
                .rating(reviewRequest.getRating())
                .customerName(reviewRequest.getCustomerName() != null ? reviewRequest.getCustomerName() : "Anonymous")
                .comment(reviewRequest.getComment() != null ? reviewRequest.getComment() : "")
                .visiblePublic(reviewRequest.getVisiblePublic() != null ? reviewRequest.getVisiblePublic() : true)
                .build();
        review = reviewRepository.save(review);
        return ReviewResponse.simple(review);
    }

    @Override
    public List<ReviewResponse> getAllReviews() {
        List<Review> reviews = reviewRepository.findAll();
        return reviews.stream().map(ReviewResponse::simple).toList();
    }
}
