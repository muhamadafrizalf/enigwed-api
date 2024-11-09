package com.enigwed.repository;

import com.enigwed.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
    Optional<BankAccount> findByIdAndDeletedAtIsNull(String id);
    List<BankAccount> findByWeddingOrganizerIdAndDeletedAtIsNull(String weddingOrganizerId);
    Integer countByWeddingOrganizerIdAndDeletedAtIsNull(String weddingOrganizerId);
}