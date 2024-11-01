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

    @Query(value = "SELECT * FROM wedding_organizer t " +
            "WHERE to_tsvector('english', t.name || ' ' || t.phone || ' ' || t.description || ' ' || t.address || ' ' || t.city.name) @@ to_tsquery(:keyword) " +
            "AND t.deleted_at IS NULL", nativeQuery = true)
    List<WeddingOrganizer> searchWeddingOrganizer(@Param("keyword") String keyword);


}
