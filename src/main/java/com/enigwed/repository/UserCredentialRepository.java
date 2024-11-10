package com.enigwed.repository;

import com.enigwed.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByEmailAndDeletedAtIsNull(String email);
    Optional<UserCredential> findByIdAndDeletedAtIsNull(String id);
    List<UserCredential> findByDeletedAtIsNullAndActiveIsTrue();
    List<UserCredential> findByDeletedAtIsNullAndActiveIsTrueAndActiveUntilBefore(LocalDateTime now);
}
