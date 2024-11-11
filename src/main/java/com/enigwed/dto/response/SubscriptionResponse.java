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
    private WeddingOrganizerResponse weddingOrganizer;
    private SubscriptionPackageResponse subscriptionPacket;
    private Double totalPaid;
    private ESubscriptionPaymentStatus paymentStatus;
    private ImageResponse paymentImage;


    public static SubscriptionResponse all(Subscription subscription) {
        SubscriptionResponse response = new SubscriptionResponse();
        response.setId(subscription.getId());
        response.setTransactionDate(subscription.getTransactionDate());
        response.setActiveFrom(subscription.getActiveFrom());
        response.setActiveUntil(subscription.getActiveUntil());
        response.setWeddingOrganizer(WeddingOrganizerResponse.card(subscription.getWeddingOrganizer()));
        response.setSubscriptionPacket(SubscriptionPackageResponse.from(subscription.getSubscriptionPackage()));
        response.setTotalPaid(subscription.getTotalPaid());
        response.setPaymentStatus(subscription.getStatus());
        response.setPaymentImage(ImageResponse.from(subscription.getPaymentImage()));
        return response;
    }
}
