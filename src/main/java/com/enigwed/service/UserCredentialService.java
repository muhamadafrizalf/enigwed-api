package com.enigwed.service;

import com.enigwed.entity.SubscriptionPackage;
import com.enigwed.entity.UserCredential;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserCredentialService extends UserDetailsService {
    // Use in other service
    String loadAdminId();
    UserCredential loadUserById(String id) throws UsernameNotFoundException;
    UserCredential createUser(UserCredential userCredential);
    UserCredential updateUser(UserCredential userCredential);
    UserCredential deleteUser(UserCredential userCredential);
    UserCredential activateUser(UserCredential userCredential);
    UserCredential deactivateUser(UserCredential userCredential);
    UserCredential extendActiveUntil(UserCredential userCredential, SubscriptionPackage subscriptionPackage);
}
