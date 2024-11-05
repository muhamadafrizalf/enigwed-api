package com.enigwed.dto.response;

import com.enigwed.constant.EDataType;
import com.enigwed.constant.EReceiver;
import com.enigwed.entity.Notification;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class NotificationResponse {

    private String id;

    private EReceiver receiver;

    private EDataType dataType;

    private String message;

    private String receiverId;

    private String dataId;

    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    private boolean read;

    public static NotificationResponse from(Notification notification) {
        NotificationResponse response = new NotificationResponse();
        response.setId(notification.getId());
        response.setReceiver(notification.getReceiver());
        response.setDataType(notification.getDataType());
        response.setMessage(notification.getMessage());
        response.setReceiverId(notification.getReceiverId());
        response.setDataId(notification.getDataId());
        response.setCreatedAt(notification.getCreatedAt());
        response.setReadAt(notification.getReadAt());
        response.setRead(notification.isRead());
        return response;
    }
}
