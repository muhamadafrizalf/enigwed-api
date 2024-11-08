package com.enigwed.repository;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.entity.SubscriptionPrice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPriceRepository extends JpaRepository<SubscriptionPrice, String> {
    Optional<SubscriptionPrice> findByIdAndDeletedAtIsNull(String id);
    List<SubscriptionPrice> findByDeletedAtIsNull();
    boolean existsBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength subscriptionLength);
}
