package com.enigwed.repository;

import com.enigwed.entity.BonusPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BonusPackageRepository extends JpaRepository<BonusPackage, String> {
    Optional<BonusPackage> findByIdAAndDeletedAtIsNull(String id);
    List<BonusPackage> findByDeletedAtIsNull();
    List<BonusPackage> findByWeddingOrganizerIdAndDeletedAtIsNull(String weddingOrganizerId);

    @Query("SELECT w.userCredential.id FROM BonusPackage b JOIN b.weddingOrganizer w WHERE b.id = :id")
    String findBonusPackageWeddingOrganizerUserCredentialIdById(@Param("id") String id);

    @Query("SELECT bp FROM BonusPackage bp WHERE (" +
            "LOWER(bp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND bp.deletedAt IS NULL")
    List<BonusPackage> searchBonusPackage(@Param("keyword") String keyword);
}
