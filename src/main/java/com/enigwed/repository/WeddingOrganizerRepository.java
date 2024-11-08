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

    Optional<WeddingOrganizer> findByIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(String id);
    List<WeddingOrganizer> findByDeletedAtIsNullAndUserCredentialActiveIsTrue();
    Optional<WeddingOrganizer> findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(String userCredentialId);
    Optional<WeddingOrganizer> findByUserCredentialEmailAndDeletedAtIsNull(String email);

    @Query("SELECT wo FROM WeddingOrganizer wo WHERE (" +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.province.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.regency.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.district.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND wo.deletedAt IS NULL AND wo.userCredential.active IS TRUE ")
    List<WeddingOrganizer> searchWeddingOrganizerCustomer(@Param("keyword") String keyword);

    @Query("SELECT wo FROM WeddingOrganizer wo WHERE (" +
            "LOWER(wo.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.phone) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.address) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.province.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.regency.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(wo.district.name) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    List<WeddingOrganizer> searchWeddingOrganizer(@Param("keyword") String keyword);
}
