package com.enigwed.repository;

import com.enigwed.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, String> {
    Optional<UserCredential> findByEmailAndDeletedAtIsNull(String email);
}
