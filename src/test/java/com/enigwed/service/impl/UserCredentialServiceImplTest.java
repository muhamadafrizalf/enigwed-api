package com.enigwed.service.impl;

import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.entity.UserCredential;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.UserCredentialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@TestPropertySource(properties = {
        "com.enigwed.email-admin=admin@enigwed.com",
        "com.enigwed.password-admin=admin4321"
})
@SpringBootTest
class UserCredentialServiceImplTest {

    @InjectMocks
    private UserCredentialServiceImpl userCredentialService;

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Value("${com.enigwed.email-admin}")
    private String emailAdmin;

    @Value("${com.enigwed.password-admin}")
    private String passwordAdmin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void initAdmin_AdminExists_DoesNotSaveAdmin() {
        // Arrange
        when(userCredentialRepository.findByEmailAndDeletedAtIsNull(emailAdmin)).thenReturn(Optional.of(new UserCredential()));

        // Act
        userCredentialService.initAdmin();

        // Assert
        verify(userCredentialRepository, never()).save(any(UserCredential.class));
    }

//    @Test
//    void initAdmin_AdminDoesntExist_CreateAdmin() {
//        // Arrange
//        when(userCredentialRepository.findByEmailAndDeletedAtIsNull(emailAdmin)).thenReturn(Optional.empty());
//        when(passwordEncoder.encode(passwordAdmin)).thenReturn("encodedPassword");
//
//        // Act
//        userCredentialService.initAdmin();
//
//        // Assert
//        verify(userCredentialRepository, times(1)).save(argThat(user ->
//                user.getEmail().equals(emailAdmin) &&
//                        user.getPassword().equals("encodedPassword") &&
//                        user.getRole() == ERole.ROLE_ADMIN &&
//                        user.isActive()
//        ));
//    }

    @Test
    void loadUserByUsername_EmailIsNull_ThrowsErrorResponse() {
        // Act & Assert
        ErrorResponse exception = assertThrows(ErrorResponse.class, () -> {
            userCredentialService.loadUserByUsername(null);
        });

        // Verify the exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Message.FETCHING_FAILED, exception.getMessage());
        assertEquals(ErrorMessage.ID_IS_REQUIRED, exception.getError());
    }

    @Test
    void loadUserByUsername_EmailIsEmpty_ThrowsErrorResponse() {
        // Act & Assert
        ErrorResponse exception = assertThrows(ErrorResponse.class, () -> {
            userCredentialService.loadUserByUsername("");
        });

        // Verify the exception details
        assertEquals(HttpStatus.BAD_REQUEST, exception.getHttpStatus());
        assertEquals(Message.FETCHING_FAILED, exception.getMessage());
        assertEquals(ErrorMessage.ID_IS_REQUIRED, exception.getError());
    }

    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        // Arrange
        String nonExistentEmail = "nonexistent@enigwed.com";
        when(userCredentialRepository.findByEmailAndDeletedAtIsNull(nonExistentEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userCredentialService.loadUserByUsername(nonExistentEmail);
        });

        assertEquals(nonExistentEmail, exception.getMessage());
    }

    @Test
    void create_UserAlreadyExists_ThrowsDataIntegrityViolationException() {
        // Arrange
        UserCredential existingUser = new UserCredential();
        existingUser.setEmail("existing@enigwed.com");
        when(userCredentialRepository.findByEmailAndDeletedAtIsNull(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        // Act & Assert
        DataIntegrityViolationException exception = assertThrows(DataIntegrityViolationException.class, () -> {
            userCredentialService.create(existingUser);
        });

        assertEquals(ErrorMessage.EMAIL_ALREADY_IN_USE, exception.getMessage());
    }

    @Test
    void create_SuccessfullyCreatesUser() {
        // Arrange
        UserCredential newUser = new UserCredential();
        newUser.setEmail("new@enigwed.com");
        newUser.setPassword("password123");
        when(userCredentialRepository.findByEmailAndDeletedAtIsNull(newUser.getEmail())).thenReturn(Optional.empty());
        when(userCredentialRepository.saveAndFlush(newUser)).thenReturn(newUser);

        // Act
        UserCredential createdUser = userCredentialService.create(newUser);

        // Assert
        assertEquals(newUser, createdUser);
        verify(userCredentialRepository, times(1)).saveAndFlush(newUser);
    }
}
