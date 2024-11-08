package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPriceRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.Subscription;
import com.enigwed.entity.SubscriptionPrice;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {
    List<Subscription> getSubscriptions(LocalDateTime from, LocalDateTime to);

    ApiResponse<List<SubscriptionPrice>> getSubscriptionPrices();
    ApiResponse<SubscriptionPrice> getSubscriptionPriceById(String subscriptionPriceId);
    ApiResponse<SubscriptionPrice> addSubscriptionPrice(SubscriptionPriceRequest subscriptionPriceRequest);
    ApiResponse<SubscriptionPrice> updateSubscriptionPrice(SubscriptionPriceRequest subscriptionPriceRequest);
    ApiResponse<?> deleteSubscriptionPrice(String subscriptionId);

    ApiResponse<SubscriptionResponse> paySubscription(JwtClaim userInfo, SubscriptionRequest subscriptionRequest);
    ApiResponse<List<SubscriptionResponse>> getOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest);
    ApiResponse<SubscriptionResponse> getSubscriptionById(JwtClaim userInfo, String subscriptionId);
    ApiResponse<SubscriptionResponse> confirmPaymentSubscriptionById(String subscriptionId);
    ApiResponse<List<SubscriptionResponse>> getAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest);

}
