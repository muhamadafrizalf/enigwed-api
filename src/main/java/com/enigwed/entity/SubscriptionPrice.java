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
public class SubscriptionPrice extends AuditEntity{

    @Column(name = "subscription_length")
    @Enumerated(EnumType.STRING)
    private ESubscriptionLength subscriptionLength;

    private double price;
}
