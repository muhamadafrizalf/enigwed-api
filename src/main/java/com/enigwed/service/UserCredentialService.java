package com.enigwed.service;

import com.enigwed.entity.UserCredential;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserCredentialService extends UserDetailsService {
    UserCredential findById(String id);
    UserCredential create(UserCredential userCredential);
}
