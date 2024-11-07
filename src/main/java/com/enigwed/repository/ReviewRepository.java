package com.enigwed.repository;

import com.enigwed.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {
    Optional<Review> findByOrderId(String orderId);
    List<Review> findByWeddingOrganizerId(String weddingOrganizerId);
    List<Review> findByWeddingPackageId(String weddingPackageId);
}
