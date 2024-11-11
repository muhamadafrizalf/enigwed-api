package com.enigwed.repository;

import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findAllByOrderByTransactionDateDesc();
    List<Subscription> findByWeddingOrganizerIdOrderByTransactionDateDesc(String weddingOrganizerId);
    List<Subscription> findByStatusAndTransactionDateBetween(ESubscriptionPaymentStatus status, LocalDateTime from, LocalDateTime to);
}
