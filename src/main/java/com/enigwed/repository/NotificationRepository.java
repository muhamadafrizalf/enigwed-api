package com.enigwed.repository;

import com.enigwed.constant.ENotificationChannel;
import com.enigwed.constant.EReceiver;
import com.enigwed.entity.Notification;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByReceiverIdAndChannel(String receiverId, ENotificationChannel channel, Sort sort);
    List<Notification> findByReceiverAndChannel(EReceiver receiver, ENotificationChannel channel, Sort sort);
}
