package com.enigwed.entity;

import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.constant.SPathDb;
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
@Table(name = SPathDb.SUBSCRIPTION)
public class Subscription extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @Column(name = "total_paid")
    private double totalPaid;

    @ManyToOne
    @JoinColumn(name = "subscription_packet_id")
    private SubscriptionPackage subscriptionPackage;

    @ManyToOne
    @JoinColumn(name = "payment_image")
    private Image paymentImage;

    @Enumerated(EnumType.STRING)
    private ESubscriptionPaymentStatus status;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Column(name = "active_from")
    private LocalDateTime activeFrom;

    @Column(name = "active_until")
    private LocalDateTime activeUntil;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}
