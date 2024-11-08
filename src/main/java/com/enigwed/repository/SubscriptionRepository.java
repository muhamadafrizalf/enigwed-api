package com.enigwed.repository;

import com.enigwed.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findAllByOrderByTransactionDateDesc();
    List<Subscription> findByWeddingOrganizerIdOrderByTransactionDate(String weddingOrganizerId);
}
