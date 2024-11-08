package com.enigwed.service;

import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.SubscriptionPrice;

import java.util.List;

public interface SubscriptionService {
    List<SubscriptionPrice> getSubscriptionPrices();
    SubscriptionPrice getSubscriptionPrice(String subscriptionPriceId);
    SubscriptionPrice addSubscriptionPrice(SubscriptionPrice subscriptionPrice);
    SubscriptionPrice updateSubscriptionPrice(SubscriptionPrice subscriptionPrice);
    void deleteSubscriptionPrice(String subscriptionId);

    SubscriptionResponse paySubscription(SubscriptionRequest subscriptionRequest);
    SubscriptionResponse acceptSubscriptionById(String subscriptionId);
}
