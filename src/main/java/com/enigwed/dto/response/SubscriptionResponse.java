package com.enigwed.dto.response;

import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.entity.Subscription;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionResponse {
    private String id;
    private LocalDateTime transactionDate;
    private LocalDateTime activeFrom;
    private LocalDateTime activeUntil;
    private Double totalPaid;
    private ESubscriptionPaymentStatus paymentStatus;
    private ImageResponse paymentImage;
    private WeddingOrganizerResponse weddingOrganizer;
    private SubscriptionPackageResponse subscriptionPacket;

    public static SubscriptionResponse from(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setTransactionDate(subscription.getTransactionDate());
        response.setActiveFrom(subscription.getActiveFrom());
        response.setActiveUntil(subscription.getActiveUntil());
        response.setTotalPaid(subscription.getTotalPaid());
        response.setPaymentStatus(subscription.getStatus());
        response.setPaymentImage(ImageResponse.from(subscription.getPaymentImage()));
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(subscription.getWeddingOrganizer()));
        response.setSubscriptionPacket(SubscriptionPackageResponse.card(subscription.getSubscriptionPackage()));
        return response;
    }
}
