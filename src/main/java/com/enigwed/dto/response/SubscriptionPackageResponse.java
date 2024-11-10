package com.enigwed.dto.response;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.entity.SubscriptionPackage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPackageResponse {

    private String id;
    private String name;
    private ESubscriptionLength subscriptionLength;
    private String description;
    private Double price;

    private Long orderCount;
    private Boolean popular;

    public static SubscriptionPackageResponse all(SubscriptionPackage subscriptionPackage, Map<String, Long> orders) {
        SubscriptionPackageResponse response = simple(subscriptionPackage);
        response.setOrderCount(orders.get(subscriptionPackage.getId()) != null ? orders.get(subscriptionPackage.getId()) : 0);
        return response;
    }

    public static SubscriptionPackageResponse simple(SubscriptionPackage subscriptionPackage) {
        SubscriptionPackageResponse response = new SubscriptionPackageResponse();
        response.setId(subscriptionPackage.getId());
        response.setName(subscriptionPackage.getName());
        response.setSubscriptionLength(subscriptionPackage.getSubscriptionLength());
        response.setDescription(subscriptionPackage.getDescription());
        response.setPrice(subscriptionPackage.getPrice());
        return response;
    }
}
