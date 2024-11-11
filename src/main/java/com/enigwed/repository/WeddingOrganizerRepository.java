package com.enigwed.repository;

import com.enigwed.entity.WeddingOrganizer;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
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
    Optional<WeddingOrganizer> findByUserCredentialIdAndDeletedAtIsNullAndUserCredentialActiveIsTrue(String userCredentialId);
    Optional<WeddingOrganizer> findByUserCredentialEmailAndDeletedAtIsNull(String email);

    List<WeddingOrganizer> findAll(Specification<WeddingOrganizer> spec);
    List<WeddingOrganizer> findAll(Specification<WeddingOrganizer> spec, Sort sort);
}
