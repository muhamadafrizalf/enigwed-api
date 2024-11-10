package com.enigwed.repository;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.entity.SubscriptionPackage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionPackageRepository extends JpaRepository<SubscriptionPackage, String> {
    Optional<SubscriptionPackage> findByIdAndDeletedAtIsNull(String id);
    Optional<SubscriptionPackage> findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength length);
    List<SubscriptionPackage> findByDeletedAtIsNull();
}
