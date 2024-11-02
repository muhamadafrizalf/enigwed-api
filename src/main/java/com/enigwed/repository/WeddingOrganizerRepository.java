package com.enigwed.repository;

import com.enigwed.entity.WeddingOrganizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WeddingOrganizerRepository extends JpaRepository<WeddingOrganizer, String> {
    Integer countByPhoneAndDeletedAtIsNull(String phone);
    Integer countByNibAndDeletedAtIsNull(String Nib);
    Integer countByNpwpAndDeletedAtIsNull(String Npwp);

    Optional<WeddingOrganizer> findByIdAndDeletedAtIsNull(String id);
    List<WeddingOrganizer> findByDeletedAtIsNull();
    Optional<WeddingOrganizer> findByUserCredentialIdAndDeletedAtIsNull(String userCredentialId);

    @Query("SELECT wo FROM WeddingOrganizer wo WHERE (" +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.city.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND wo.deletedAt IS NULL")
    List<WeddingOrganizer> searchWeddingOrganizer(@Param("keyword") String keyword);
}
