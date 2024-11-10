package com.enigwed.repository;

import com.enigwed.constant.EStatus;
import com.enigwed.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByBookCode(String bookCode);
    Optional<Order> findByBookCode(String bookCode);
    List<Order> findByWeddingOrganizerIdOrderByTransactionDateDesc(String weddingOrganizerId);
    List<Order> findByWeddingOrganizerIdAndStatusAndTransactionDateBetweenOrderByTransactionDateDesc(String weddingOrganizerId, EStatus status, LocalDateTime from, LocalDateTime to);
    List<Order> findAllByOrderByTransactionDateDesc();

    @Query("SELECT o FROM Order o " +
            "JOIN o.customer c " +
            "JOIN o.weddingOrganizer wo " +
            "JOIN o.weddingPackage wp " +
            "WHERE " +
            "(LOWER(o.bookCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wp.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "ORDER BY o.transactionDate DESC")
    List<Order> searchOrders(@Param("keyword") String keyword);

}
