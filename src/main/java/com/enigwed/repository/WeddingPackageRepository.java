package com.enigwed.repository;

import com.enigwed.entity.WeddingPackage;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WeddingPackageRepository extends JpaRepository<WeddingPackage, String> {
    Optional<WeddingPackage> findByIdAndDeletedAtIsNull(String id);
    List<WeddingPackage> findAll(Specification<WeddingPackage> spec);
}
