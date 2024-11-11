package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.NotificationResponse;
import com.enigwed.entity.Notification;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.NotificationRepository;
import com.enigwed.service.NotificationService;
import com.enigwed.util.AccessValidationUtil;
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
    private final AccessValidationUtil accessValidationUtil;

    private Notification findByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.ID_IS_REQUIRED);
        return notificationRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.NOTIFICATION_NOT_FOUND));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void createNotification(Notification notification) {
        notificationRepository.saveAndFlush(notification);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<NotificationResponse>> getOwnNotifications(JwtClaim userInfo) {
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));
        List<Notification> notificationList;
        if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            notificationList = notificationRepository.findByReceiverAndChannel(EReceiver.ADMIN, ENotificationChannel.SYSTEM, sort);
        } else {
            notificationList = notificationRepository.findByReceiverIdAndChannel(userInfo.getUserId(), ENotificationChannel.SYSTEM, sort);
        }
        if (notificationList == null || notificationList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), SMessage.NO_NOTIFICATION_FOUND);
        }
        List<NotificationResponse> response = notificationList.stream().map(NotificationResponse::from).toList();
        return ApiResponse.success(response, SMessage.NOTIFICATION_FOUNDS);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<NotificationResponse> readNotification(JwtClaim userInfo, String id) {
        try {
            /* LOAD NOTIFICATION */
            // ErrorResponse //
            Notification notification = findByIdOrThrow(id);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, notification);

            /* SET READ */
            notification.setRead(true);
            notification.setReadAt(LocalDateTime.now());

            /* SAVE NOTIFICATION */
            notification = notificationRepository.save(notification);

            /* MAP RESPONSE */
            NotificationResponse response = NotificationResponse.from(notification);
            return ApiResponse.success(response, SMessage.NOTIFICATION_READ);

        } catch (AccessDeniedException e) {
            log.error("Access denied while updating notification status: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.UPDATE_FAILED, e.getMessage());
        } catch (ErrorResponse e) {
            log.error("Error while updating notification status: {}", e.getMessage());
            throw e;
        }
    }

    // For Development Use
    @Transactional(readOnly = true)
    @Override
    public List<Notification> getAllNotifications() {
        Sort sort = Sort.by(Sort.Order.asc("read"), Sort.Order.desc("createdAt"));
        return notificationRepository.findAll(sort);
    }
}
