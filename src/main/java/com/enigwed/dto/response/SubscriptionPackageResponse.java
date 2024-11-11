package com.enigwed.dto.response;

import com.enigwed.constant.ESubscriptionLength;
import com.enigwed.entity.SubscriptionPackage;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionPackageResponse {
    private String id;
    private String name;
    private ESubscriptionLength subscriptionLength;
    private String description;
    private Double price;
    private Boolean popular;

    public static SubscriptionPackageResponse simple(SubscriptionPackage subscriptionPackage) {
        SubscriptionPackageResponse response = new SubscriptionPackageResponse();
        response.setId(subscriptionPackage.getId());
        response.setName(subscriptionPackage.getName());
        response.setSubscriptionLength(subscriptionPackage.getSubscriptionLength());
        response.setDescription(subscriptionPackage.getDescription());
        return response;
    }

    public static SubscriptionPackageResponse all(SubscriptionPackage subscriptionPackage) {
        SubscriptionPackageResponse response = SubscriptionPackageResponse.simple(subscriptionPackage);
        response.setPrice(subscriptionPackage.getPrice());
        response.setPopular(subscriptionPackage.isPopular());
        return response;
    }
}
