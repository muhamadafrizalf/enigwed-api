package com.enigwed.dto.request;

import com.enigwed.constant.Constraint;
import com.enigwed.constant.ESubscriptionLength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPacketRequest {

    private String id;

    @NotBlank(message = Constraint.SUBSCRIPTION_PACKET_NAME_BLANK)
    private String name;

    @NotNull
    private ESubscriptionLength subscriptionLength;

    @Positive(message = Constraint.PRICE_POSITIVE)
    private double price;

}
