package com.enigwed.repository;

import com.enigwed.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {
    Optional<Product> findByIdAndDeletedAtIsNull(String id);
    List<Product> findByWeddingOrganizerIdAndDeletedAtIsNullOrderByPriceAsc(String weddingOrganizerId);

    @Query("SELECT bp FROM Product bp " +
            "WHERE bp.weddingOrganizer.id = :weddingOrganizerId AND bp.deletedAt IS NULL AND (" +
            "LOWER(bp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Product> findByWeddingOrganizerIdAndKeyword(
            @Param("weddingOrganizerId") String weddingOrganizerId,
            @Param("keyword") String keyword
    );

    // FOR DEVELOPMENT USE
    List<Product> findByDeletedAtIsNull();

    @Query("SELECT bp FROM Product bp WHERE (" +
            "LOWER(bp.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(bp.description) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") AND bp.deletedAt IS NULL")
    List<Product> searchBonusPackage(@Param("keyword") String keyword);

}
