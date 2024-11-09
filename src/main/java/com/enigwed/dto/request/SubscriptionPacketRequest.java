package com.enigwed.dto.request;

import com.enigwed.constant.SConstraint;
import com.enigwed.constant.ESubscriptionLength;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.enigwed.constant.SConstraint.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubscriptionPacketRequest {

    private String id;

    @NotBlank(message = SUBSCRIPTION_PACKAGE_NAME_BLANK)
    private String name;

    @NotNull(message = SUBSCRIPTION_PACKAGE_LENGTH_NULL)
    private ESubscriptionLength subscriptionLength;

    @Positive(message = PRICE_INVALID)
    @NotNull(message = PRICE_NULL)
    private Double price;

}
