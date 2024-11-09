package com.enigwed.entity;

import com.enigwed.constant.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = SPathDb.NOTIFICATION)
public class Notification extends BaseEntity {

    @Enumerated(EnumType.STRING)
    private ENotificationChannel channel;

    @Enumerated(EnumType.STRING)
    private ENotificationType type;

    @Enumerated(EnumType.STRING)
    private EReceiver receiver;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_type")
    private EDataType dataType;

    private String message;

    @Column(name = "receiver_id")
    private String receiverId;

    @Column(name = "data_id")
    private String dataId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    private boolean read;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        read = false;
    }
}
