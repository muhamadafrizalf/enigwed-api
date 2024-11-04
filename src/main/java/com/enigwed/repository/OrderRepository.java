package com.enigwed.repository;

import com.enigwed.constant.EStatus;
import com.enigwed.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByBookCode(String bookCode);
    Optional<Order> findByBookCode(String bookCode);
    List<Order> findByWeddingOrganizerId(String weddingOrganizerId);
    List<Order> findByWeddingOrganizerIdAndStatus(String weddingOrganizerId, EStatus status);
    List<Order> findByWeddingOrganizerIdAndWeddingPackageId(String weddingOrganizerId, String weddingPackageId);
    List<Order> findByWeddingOrganizerIdAndTransactionDateBetween(String weddingOrganizerId, LocalDateTime from, LocalDateTime to);
    List<Order> findByWeddingPackageId(String weddingPackageId);
    List<Order> findByStatus(EStatus status);
    List<Order> findByTransactionDateBetween(LocalDateTime from, LocalDateTime to);
    List<Order> findByTransactionFinishDateBetween(LocalDateTime start, LocalDateTime end);
}
