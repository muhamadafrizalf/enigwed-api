package com.enigwed.repository;

import com.enigwed.constant.ENotificationChannel;
import com.enigwed.entity.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotificationRepositoryTest {

    @Mock
    private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findByReceiverIdAndChannel_ReceiverIdAndChannelExist_ReturnListOfNotifications() {
        // Arrange
        Notification notification = Notification.builder()
                .receiverId("123")
                .channel(ENotificationChannel.SYSTEM)
                .build();

        List<Notification> expect = List.of(notification);

        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));

        Mockito.when(notificationRepository.findByReceiverIdAndChannel("123", ENotificationChannel.SYSTEM, sort)).thenReturn(expect);

        // Act
        List<Notification> actual = notificationRepository.findByReceiverIdAndChannel("123", ENotificationChannel.SYSTEM, sort);

        // Assert
        assertFalse(actual.isEmpty());
        assertTrue(actual.contains(notification));
        Mockito.verify(notificationRepository, Mockito.times(1)).findByReceiverIdAndChannel("123", ENotificationChannel.SYSTEM, sort);
    }

    @Test
    void findByReceiverIdAndChannel_ReceiverIdOrChannelDoesNotExist_ReturnEmptyList() {
        // Arrange
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));

        Mockito.when(notificationRepository.findByReceiverIdAndChannel(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(List.of());

        // Act
        List<Notification> actual = notificationRepository.findByReceiverIdAndChannel("123", ENotificationChannel.SYSTEM, sort);

        assertTrue(actual.isEmpty());
        Mockito.verify(notificationRepository, Mockito.times(1)).findByReceiverIdAndChannel(Mockito.any(), Mockito.any(), Mockito.any());
    }

}