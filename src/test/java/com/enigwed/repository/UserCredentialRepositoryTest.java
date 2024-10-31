package com.enigwed.repository;

import com.enigwed.entity.UserCredential;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserCredentialRepositoryTest {

    @Mock
    private UserCredentialRepository userCredentialRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByEmailAndDeletedAtIsNull_UserExists_ReturnUser() {
        // Arrange
        UserCredential userExist = UserCredential.builder()
                .email("test@email.com")
                .deletedAt(null)
                .build();

        Mockito.when(userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com")).thenReturn(Optional.ofNullable(userExist));

        // Act
        Optional<UserCredential> foundUser = userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("test@email.com", foundUser.get().getEmail());
        Mockito.verify(userCredentialRepository, Mockito.times(1)).findByEmailAndDeletedAtIsNull("test@email.com");
    }

    @Test
    void findByEmailAndDeletedAtIsNull_UserDoesNotExist_ReturnNull() {
        // Arrange
        Mockito.when(userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com")).thenReturn(Optional.empty());

        // Act
        Optional<UserCredential> foundUser = userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com");

        // Assert
        assertFalse(foundUser.isPresent());
        Mockito.verify(userCredentialRepository, Mockito.times(1)).findByEmailAndDeletedAtIsNull("test@email.com");
    }

    @Test
    void findByEmailAndDeletedAtIsNotNull_UserDeleted_ReturnNull() {
        // Arrange
        UserCredential userDeleted = UserCredential.builder()
                .email("test@email.com")
                .deletedAt(LocalDateTime.now())
                .build();

        Mockito.when(userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com")).thenReturn(Optional.empty());

        // Act
        Optional<UserCredential> foundUser = userCredentialRepository.findByEmailAndDeletedAtIsNull("test@email.com");

        // Assert
        assertFalse(foundUser.isPresent());
        Mockito.verify(userCredentialRepository, Mockito.times(1)).findByEmailAndDeletedAtIsNull("test@email.com");
    }
}