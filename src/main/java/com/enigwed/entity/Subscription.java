package com.enigwed.entity;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.constant.PathDb;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = PathDb.SUBSCRIPTION)
public class Subscription extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "wedding_organizer_id")
    private WeddingOrganizer weddingOrganizer;

    @Column(name = "total_paid")
    private double totalPaid;

    @Column(name = "subscription_length")
    @Enumerated(EnumType.STRING)
    private ESubscriptionLength subscriptionLength;

    @ManyToOne
    @JoinColumn(name = "payment_image")
    private Image paymentImage;

    private boolean accepted;
}
