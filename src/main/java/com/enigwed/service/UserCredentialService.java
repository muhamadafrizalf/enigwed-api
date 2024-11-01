package com.enigwed.service;

import com.enigwed.entity.UserCredential;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserCredentialService extends UserDetailsService {
    UserCredential loadUserById(String id);
    UserCredential createUser(UserCredential userCredential);
    UserCredential updateUser(UserCredential userCredential);
    UserCredential deleteUser(String id);
    UserCredential activateUser(UserCredential userCredential);
}
