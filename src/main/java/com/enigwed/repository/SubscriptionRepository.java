package com.enigwed.repository;

import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    List<Subscription> findAllByOrderByTransactionDateDesc();
    List<Subscription> findByWeddingOrganizerIdOrderByTransactionDate(String weddingOrganizerId);
    List<Subscription> findByStatusAndTransactionDateBetween(ESubscriptionPaymentStatus status, LocalDateTime from, LocalDateTime to);

    @Query("SELECT sp.id, COUNT(s) " +
            "FROM Subscription s " +
            "JOIN s.subscriptionPackage sp " +
            "WHERE s.status = :status " +
            "GROUP BY sp.id")
    Map<String, Long> countSubscriptionsGroupedBySubscriptionPacket(ESubscriptionPaymentStatus status);
}
