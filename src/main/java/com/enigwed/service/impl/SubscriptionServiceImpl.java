package com.enigwed.service.impl;

import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.SubscriptionPrice;
import com.enigwed.repository.SubscriptionPriceRepository;
import com.enigwed.repository.SubscriptionRepository;
import com.enigwed.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPriceRepository subscriptionPriceRepository;

    @Override
    public List<SubscriptionPrice> getSubscriptionPrices() {
        return subscriptionPriceRepository.findByDeletedAtIsNull();
    }

    @Override
    public SubscriptionPrice getSubscriptionPrice(String subscriptionPriceId) {
        return subscriptionPriceRepository.findByIdAndDeletedAtIsNull(subscriptionPriceId).orElse(null);
    }

    @Override
    public SubscriptionPrice addSubscriptionPrice(SubscriptionPrice subscriptionPrice) {

        return null;
    }

    @Override
    public SubscriptionPrice updateSubscriptionPrice(SubscriptionPrice subscriptionPrice) {
        return null;
    }

    @Override
    public void deleteSubscriptionPrice(String subscriptionId) {

    }

    @Override
    public SubscriptionResponse paySubscription(SubscriptionRequest subscriptionRequest) {
        return null;
    }

    @Override
    public SubscriptionResponse acceptSubscriptionById(String subscriptionId) {
        return null;
    }
}
