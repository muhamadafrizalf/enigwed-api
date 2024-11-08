package com.enigwed.repository;

import com.enigwed.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByBookCode(String bookCode);
    Optional<Order> findByBookCode(String bookCode);
    List<Order> findByWeddingOrganizerIdOrderByTransactionDateDesc(String weddingOrganizerId);
    List<Order> findAllByOrderByTransactionDateDesc();
}
