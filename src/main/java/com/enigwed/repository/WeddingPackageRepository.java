package com.enigwed.repository;

import com.enigwed.entity.WeddingPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeddingPackageRepository extends JpaRepository<WeddingPackage, String> {
    Optional<WeddingPackage> findByIdAndDeletedAtIsNull(String id);
    List<WeddingPackage> findByDeletedAtIsNull();
    List<WeddingPackage> findByWeddingOrganizerIdAndDeletedAtIsNull(String weddingOrganizerId);

    @Query("SELECT wp FROM WeddingPackage wp " +
            "JOIN wp.province p " +
            "JOIN wp.regency r " +
            "JOIN wp.weddingOrganizer wo " +
            "WHERE (" +
            "LOWER(wp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND wp.deletedAt IS NULL")
    List<WeddingPackage> searchWeddingPackage(@Param("keyword") String keyword);

    @Query("SELECT wp FROM WeddingPackage wp " +
            "JOIN wp.province p " +
            "JOIN wp.regency r " +
            "JOIN wp.weddingOrganizer wo " +
            "WHERE wo.id = :weddingOrganizerId AND (" +
            "LOWER(wp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND wp.deletedAt IS NULL")
    List<WeddingPackage> findByWeddingOrganizerIdAndKeyword(
            @Param("weddingOrganizerId") String weddingOrganizerId,
            @Param("keyword") String keyword
    );

    @Query("SELECT wp FROM WeddingPackage wp " +
            "JOIN wp.province p " +
            "JOIN wp.regency r " +
            "JOIN wp.weddingOrganizer wo " +
            "WHERE (" +
            "LOWER(wp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wp.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    List<WeddingPackage> searchAllWeddingPackages(@Param("keyword") String keyword);
}
