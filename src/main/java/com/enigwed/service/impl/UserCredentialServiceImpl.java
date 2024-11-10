package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.entity.Notification;
import com.enigwed.entity.SubscriptionPackage;
import com.enigwed.entity.UserCredential;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.UserCredentialRepository;
import com.enigwed.service.NotificationService;
import com.enigwed.service.UserCredentialService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCredentialServiceImpl implements UserCredentialService {
    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

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

    private void sendNotification(UserCredential user, String message) {
        Notification notification = Notification.builder()
                .channel(ENotificationChannel.SYSTEM)
                .type(ENotificationType.SUBSCRIPTION_REMAINING)
                .receiver(EReceiver.WEDDING_ORGANIZER)
                .receiverId(user.getId())
                .dataType(EDataType.NO_DATA)
                .message(message)
                .build();
        notificationService.createNotification(notification);
        /*

            Create notification for channel email[SOON]

        */
    }

    /**
     * Scheduled method that sends notifications to users 7 days before the start of the next month.
     * Runs at midnight on the 25th of each month.
     */
    @Scheduled(cron = "0 0 0 25 * *")
    public void sendNotificationEachMonth() {
        LocalDateTime now = LocalDateTime.now();
        List<UserCredential> userCredentialList = userCredentialRepository.findByDeletedAtIsNullAndActiveIsTrue();

        for (UserCredential user : userCredentialList) {
            LocalDateTime activeUntil = user.getActiveUntil();
            long remainingMonths = ChronoUnit.MONTHS.between(now, activeUntil);

            if (remainingMonths < 1) {
                sendNotification(user, SNotificationMessage.SUBSCRIPTION_EXPIRED);
            } else if (remainingMonths < 4) {
                sendNotification(user, SNotificationMessage.RENEW_SUBSCRIPTION((int) remainingMonths));
            } else {
                sendNotification(user, SNotificationMessage.LOYAL_CUSTOMER((int) remainingMonths));
            }
        }
    }

    /**
     * Scheduled method that runs on the 1st of each month to deactivate users.
     */
    @Transactional
    public void deactivateUserEachMonth() {
        LocalDateTime now = LocalDateTime.now();
        List<UserCredential> userCredentialList = userCredentialRepository.findByDeletedAtIsNullAndActiveIsTrueAndActiveUntilBefore(now);

        if (userCredentialList.isEmpty()) {
            log.info("No users to deactivate.");
        } else {
            for (UserCredential user : userCredentialList) {
                // Deactivate user
                user.setActive(false);
                userCredentialRepository.save(user);  // Save changes

                // Optionally, log each deactivated user
                log.info("User with email {} has been deactivated.", user.getEmail());
            }
        }
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential deactivateUser(UserCredential userCredential) {
        /* SET ACTIVE FALSE */
        userCredential.setActive(false);

        return userCredentialRepository.saveAndFlush(userCredential);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public UserCredential extendActiveUntil(UserCredential userCredential, SubscriptionPackage subscriptionPackage) {
        /* SET ACTIVE TRUE */
        userCredential.setActive(true);

        /* EXTEND ACTIVE UNTIL */
        // Check if the current activeUntil is already expired
        if (userCredential.getActiveUntil() == null || userCredential.getActiveUntil().isBefore(LocalDateTime.now())) {
            userCredential.setActiveUntil(LocalDateTime.now()
                    .plusMonths(subscriptionPackage.getSubscriptionLength().getMonths())
                    .withDayOfMonth(1)
                    .withHour(0)
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0)
            );
        } else {
            userCredential.setActiveUntil(userCredential.getActiveUntil()
                    .plusMonths(subscriptionPackage.getSubscriptionLength().getMonths())
            );
        }

        return userCredentialRepository.saveAndFlush(userCredential);
    }
}
