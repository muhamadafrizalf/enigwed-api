package com.enigwed.entity;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.constant.SPathDb;
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
@Table(name = SPathDb.SUBSCRIPTION_PRICE)
public class SubscriptionPackage extends AuditEntity{

    private String name;

    @Column(name = "subscription_length")
    @Enumerated(EnumType.STRING)
    private ESubscriptionLength subscriptionLength;

    private String description;

    private double price;

    private boolean popular = false;

    @PrePersist
    @PreUpdate
    public void updateDescription() {
        super.onUpdate();
        if (subscriptionLength != null) {
            this.description = subscriptionLength.getDescription();
        }
    }
}
