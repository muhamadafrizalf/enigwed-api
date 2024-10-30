package com.enigwed.repository;

import com.enigwed.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByIdAndDeletedAtIsNull(String id);
    Optional<UserCredential> findByEmailAndDeletedAtIsNull(String email);
    List<UserCredential> findByDeletedAtIsNull();
}
