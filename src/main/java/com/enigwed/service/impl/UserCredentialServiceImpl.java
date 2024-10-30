package com.enigwed.service.impl;

import com.enigwed.entity.UserCredential;
import com.enigwed.repository.UserCredentialRepository;
import com.enigwed.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userCredentialRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public UserCredential findById(String id) {
        return userCredentialRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(id));
    }
}
