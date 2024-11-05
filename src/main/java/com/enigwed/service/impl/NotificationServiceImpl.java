package com.enigwed.service.impl;

import com.enigwed.constant.ENotificationChannel;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.NotificationResponse;
import com.enigwed.entity.Notification;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.NotificationRepository;
import com.enigwed.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    private Notification findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.ID_IS_REQUIRED);
        return notificationRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.NOTIFICATION_NOT_FOUND));
    }

    private void validateUserAccess(JwtClaim userInfo, Notification notification) throws AccessDeniedException {
        String userId = notification.getReceiverId();
        if (userInfo.getUserId().equals(userId)) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createNotification(Notification notification) {
        notificationRepository.saveAndFlush(notification);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<NotificationResponse> readNotification(JwtClaim userInfo, String id) {
        try {
            // ErrorResponse
            Notification notification = findByIdOrThrow(id);
            // AccessDeniedException
            validateUserAccess(userInfo, notification);
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());
            notification = notificationRepository.save(notification);
            NotificationResponse response = NotificationResponse.from(notification);
            return ApiResponse.success(response, Message.NOTIFICATION_READ);
        } catch (AccessDeniedException e) {
            log.error("Access denied during updating notification status: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error during updating notification status: {}", e.getMessage());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<NotificationResponse>> getOwnNotifications(JwtClaim userInfo) {
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));
        List<Notification> notificationList = notificationRepository.findByReceiverIdAndChannel(userInfo.getUserId(), ENotificationChannel.SYSTEM, sort);
        if (notificationList == null || notificationList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), Message.NO_NOTIFICATION_FOUND);
        }
        List<NotificationResponse> response = notificationList.stream().map(NotificationResponse::from).toList();
        return ApiResponse.success(response, Message.NOTIFICATION_FOUNDS);
    }

    // For Development Use
    @Transactional(readOnly = true)
    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }
}
