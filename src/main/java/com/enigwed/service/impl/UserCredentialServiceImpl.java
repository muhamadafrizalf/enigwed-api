package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.entity.UserCredential;
import com.enigwed.repository.UserCredentialRepository;
import com.enigwed.service.UserCredentialService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${com.enigwed.emaail-admin}")
    private String emailAdmin;

    @Value("${com.enigwed.password-admin}")
    private String passwordAdmin;

    @PostConstruct
    public void initAdmin() {
        if (userCredentialRepository.findByEmailAndDeletedAtIsNull(emailAdmin).isPresent()) return;

        UserCredential admin = UserCredential.builder()
                .email(emailAdmin)
                .password(passwordEncoder.encode(passwordAdmin))
                .role(ERole.ROLE_ADMIN)
                .isActive(true)
                .build();

        userCredentialRepository.save(admin);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userCredentialRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Override
    public UserCredential loadUserById(String id) {
        return userCredentialRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential create(UserCredential userCredential) {
        if (userCredentialRepository.findByEmailAndDeletedAtIsNull(userCredential.getEmail()).isPresent()) {
            throw new DataIntegrityViolationException(ErrorMessage.EMAIL_ALREADY_IN_USE);
        }

        return userCredentialRepository.saveAndFlush(userCredential);
    }
}
