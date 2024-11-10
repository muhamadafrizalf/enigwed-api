package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPacketRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.Subscription;
import com.enigwed.entity.SubscriptionPacket;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {
    List<Subscription> getSubscriptions(LocalDateTime from, LocalDateTime to);

    ApiResponse<List<SubscriptionPacket>> getSubscriptionPrices();
    ApiResponse<SubscriptionPacket> getSubscriptionPriceById(String subscriptionPriceId);
    ApiResponse<SubscriptionPacket> addSubscriptionPrice(SubscriptionPacketRequest subscriptionPacketRequest);
    ApiResponse<SubscriptionPacket> updateSubscriptionPrice(SubscriptionPacketRequest subscriptionPacketRequest);
    ApiResponse<?> deleteSubscriptionPrice(String subscriptionId);

    ApiResponse<List<SubscriptionResponse>> getOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest);
    ApiResponse<List<SubscriptionResponse>> getActiveSubscriptions(JwtClaim userInfo, PagingRequest pagingRequest);
    ApiResponse<SubscriptionResponse> paySubscription(JwtClaim userInfo, SubscriptionRequest subscriptionRequest);

    ApiResponse<List<SubscriptionResponse>> getAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest);
    ApiResponse<SubscriptionResponse> getSubscriptionById(JwtClaim userInfo, String subscriptionId);
    ApiResponse<SubscriptionResponse> confirmPaymentSubscriptionById(String subscriptionId);

}
