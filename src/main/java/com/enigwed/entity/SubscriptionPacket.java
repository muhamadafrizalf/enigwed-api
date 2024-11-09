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
@Table(name = PathDb.SUBSCRIPTION_PRICE)
public class SubscriptionPacket extends AuditEntity{

    private String name;

    @Column(name = "subscription_length")
    @Enumerated(EnumType.STRING)
    private ESubscriptionLength subscriptionLength;

    private String description = subscriptionLength.getDescription();

    private double price;
}
