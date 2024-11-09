package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.NotificationResponse;
import com.enigwed.entity.Notification;

import java.util.List;

public interface NotificationService {
    // Use in other service
    void createNotification(Notification notification);

    ApiResponse<NotificationResponse> readNotification(JwtClaim userInfo, String id);
    ApiResponse<List<NotificationResponse>> getOwnNotifications(JwtClaim userInfo);

    // For Development Use
    List<Notification> getAllNotifications();
}
