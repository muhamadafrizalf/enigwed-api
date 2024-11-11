package com.enigwed.repository;

import com.enigwed.constant.EStatus;
import com.enigwed.entity.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, String> {
    boolean existsByBookCode(String bookCode);
    Optional<Order> findByBookCode(String bookCode);
    List<Order> findAll(Specification<Order> spec, Sort sort);
    List<Order> findByWeddingOrganizerIdAndStatusAndTransactionDateBetweenOrderByTransactionDateDesc(String weddingOrganizerId, EStatus status, LocalDateTime from, LocalDateTime to);
}
