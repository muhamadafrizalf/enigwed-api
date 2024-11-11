package com.enigwed.service;

import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPackageRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionPackageResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.Subscription;

import java.time.LocalDateTime;
import java.util.List;

public interface SubscriptionService {
    // Use in other service
    List<Subscription> loadConfirmedSubscription(LocalDateTime from, LocalDateTime to);
    // Wedding organizer & admin
    ApiResponse<List<SubscriptionPackageResponse>> findSubscriptionPackages();
    ApiResponse<SubscriptionPackageResponse> findSubscriptionPackageById(String subscriptionPackageId);
    // Admin
    ApiResponse<SubscriptionPackageResponse> createSubscriptionPackage(SubscriptionPackageRequest subscriptionPackageRequest);
    ApiResponse<SubscriptionPackageResponse> updateSubscriptionPackage(SubscriptionPackageRequest subscriptionPackageRequest);
    ApiResponse<?> deleteSubscriptionPackage(String subscriptionId);
    // Wedding organizer

    ApiResponse<List<SubscriptionResponse>> findOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest);
    ApiResponse<List<SubscriptionResponse>> findOwnActiveSubscriptions(JwtClaim userInfo, PagingRequest pagingRequest);
    ApiResponse<SubscriptionResponse> findOwnSubscriptionById(JwtClaim userInfo, String subscriptionId);
    ApiResponse<SubscriptionResponse> paySubscription(JwtClaim userInfo, SubscriptionRequest subscriptionRequest);
    // Admin
    ApiResponse<List<SubscriptionResponse>> findAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest);
    ApiResponse<List<SubscriptionResponse>> findAllActiveSubscriptions(PagingRequest pagingRequest, String weddingOrganizerId);
    ApiResponse<SubscriptionResponse> findSubscriptionById(String subscriptionId);
    ApiResponse<SubscriptionResponse> confirmSubscriptionPaymentById(String subscriptionId);

}
