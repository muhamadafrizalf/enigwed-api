package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.SErrorMessage;
import com.enigwed.constant.SMessage;
import com.enigwed.entity.UserCredential;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.UserCredentialRepository;
import com.enigwed.service.UserCredentialService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserCredentialServiceImpl implements UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${com.enigwed.email-admin}")
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
                .active(true)
                .build();

        userCredentialRepository.save(admin);
    }

    @Transactional(readOnly = true)
    @Override
    public String loadAdminId() {
        UserCredential admin = userCredentialRepository.findByEmailAndDeletedAtIsNull(emailAdmin).orElse(null);
        return admin != null ? admin.getId() : "";
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (email == null || email.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.EMAIL_IS_REQUIRED);
        return userCredentialRepository.findByEmailAndDeletedAtIsNull(email).orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Transactional(readOnly = true)
    @Override
    public UserCredential loadUserById(String id) throws UsernameNotFoundException {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.USER_CREDENTIAL_ID_IS_REQUIRED);
        return userCredentialRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new UsernameNotFoundException(id));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential createUser(UserCredential userCredential) {
        if (userCredentialRepository.findByEmailAndDeletedAtIsNull(userCredential.getEmail()).isPresent()) throw new DataIntegrityViolationException(SErrorMessage.EMAIL_ALREADY_IN_USE);
        return userCredentialRepository.saveAndFlush(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential updateUser(UserCredential userCredential) {
        UserCredential possibleConflict = userCredentialRepository.findByEmailAndDeletedAtIsNull(userCredential.getEmail()).orElse(null);
        if (possibleConflict != null && !possibleConflict.getId().equals(userCredential.getId())) throw new DataIntegrityViolationException(SErrorMessage.EMAIL_ALREADY_IN_USE);
        return userCredentialRepository.saveAndFlush(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential deleteUser(UserCredential userCredential) {
        userCredential.setEmail("deleted_" + userCredential.getEmail());
        userCredential.setDeletedAt(LocalDateTime.now());
        userCredential.setActive(false);
        return userCredentialRepository.saveAndFlush(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential activateUser(UserCredential userCredential) {
        /* SET ACTIVE TRUE */
        userCredential.setActive(true);

        // Check if the current activeUntil is more than 1 month ahead
        if (userCredential.getActiveUntil() == null || userCredential.getActiveUntil().isBefore(LocalDateTime.now().plusMonths(1))) {
            /* SET ACTIVE UNTIL FIRST DAY OF NEXT MONTH */
            userCredential.setActiveUntil(LocalDateTime.now()
                    .plusMonths(1)
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
            );
        }

        return userCredentialRepository.saveAndFlush(userCredential);
    }


    @Override
    public UserCredential deactivateUser(UserCredential userCredential) {
        /* SET ACTIVE FALSE */
        userCredential.setActive(false);
        return userCredentialRepository.saveAndFlush(userCredential);
    }
}
