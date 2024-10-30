package com.enigwed.repository;

import com.enigwed.entity.WeddingOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeddingOrganizerRepository extends JpaRepository<WeddingOrganizer, String> {
    Optional<WeddingOrganizer> findByIdAndDeletedAtIsNull(String id);
    List<WeddingOrganizer> findByDeletedAtIsNull();

    @Query("SELECT t FROM WeddingOrganizer t " +
            "WHERE (LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.address) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(t.city.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND t.deletedAt IS NULL")
    List<WeddingOrganizer> searchWeddingOrganizer(@Param("keyword") String keyword);

}
