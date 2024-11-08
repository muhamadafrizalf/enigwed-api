package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPriceRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.Subscription;
import com.enigwed.entity.SubscriptionPrice;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.SubscriptionPriceRepository;
import com.enigwed.repository.SubscriptionRepository;
import com.enigwed.service.SubscriptionService;
import com.enigwed.service.WeddingOrganizerService;
import com.enigwed.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPriceRepository subscriptionPriceRepository;
    private final WeddingOrganizerService weddingOrganizerService;
    private final ValidationUtil validationUtil;

    @Value("${com.enigwed.subscription-price-one-month}")
    private double oneMonthPrice;
    @Value("${com.enigwed.subscription-price-two-months}")
    private double twoMonthsPrice;
    @Value("${com.enigwed.subscription-price-three-months}")
    private double threeMonthsPrice;
    @Value("${com.enigwed.subscription-price-four-months}")
    private double fourMonthsPrice;
    @Value("${com.enigwed.subscription-price-five-months}")
    private double fiveMonthsPrice;
    @Value("${com.enigwed.subscription-price-six-months}")
    private double sixMonthsPrice;

    @PostConstruct
    public void init() {
        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.A_MONTH).isEmpty()) {
            SubscriptionPrice oneMonth = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.A_MONTH)
                    .price(oneMonthPrice)
                    .build();
            subscriptionPriceRepository.save(oneMonth);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.TWO_MONTHS).isEmpty()) {
            SubscriptionPrice twoMonths = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.TWO_MONTHS)
                    .price(twoMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(twoMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.THREE_MONTHS).isEmpty()) {
            SubscriptionPrice threeMonths = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.THREE_MONTHS)
                    .price(threeMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(threeMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FOUR_MONTHS).isEmpty()) {
            SubscriptionPrice fourMonths = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.FOUR_MONTHS)
                    .price(fourMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(fourMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FIVE_MONTHS).isEmpty()) {
            SubscriptionPrice fiveMonths = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.FIVE_MONTHS)
                    .price(fiveMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(fiveMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.SIX_MONTHS).isEmpty()) {
            SubscriptionPrice sixMonths = SubscriptionPrice.builder()
                    .subscriptionLength(ESubscriptionLength.SIX_MONTHS)
                    .price(sixMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(sixMonths);
        }
    }

    private SubscriptionPrice findSubscriptionPriceByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_PRICE_ID_IS_REQUIRED);
        return subscriptionPriceRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_PRICE_NOT_FOUND));
    }

    private List<Subscription> filterResult(FilterRequest filter, List<Subscription> list) {
        return list.stream()
                .filter(item ->
                        (filter.getStartDate() == null || !item.getTransactionDate().isBefore(filter.getStartDate())) &&
                        (filter.getEndDate() == null || !item.getTransactionDate().isAfter(filter.getEndDate()))
                )
                .toList();
    }

    private List<Subscription> filterByStatus(FilterRequest filter, List<Subscription> list) {
        return list.stream()
                .filter(item -> filter.getOrderStatus() == null || item.getStatus().equals(filter.getSubscriptionPaymentStatus()))
                .toList();
    }

    private Map<String, Integer> countByStatus(List<Subscription> list) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (ESubscriptionPaymentStatus status : ESubscriptionPaymentStatus.values()) {
            map.put(status.name(), 0);
        }
        for (Subscription subscriptionr : list) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(subscriptionr.getStatus().name(), map.get(subscriptionr.getStatus().name()) + 1);
        }
        return map;
    }

    private void validateUserAccess(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(ErrorMessage.ACCESS_DENIED);
    }

    @Override
    public ApiResponse<List<SubscriptionPrice>> getSubscriptionPrices() {
        List<SubscriptionPrice> response = subscriptionPriceRepository.findByDeletedAtIsNull();
        if (response == null || response.isEmpty())
            return ApiResponse.success(new ArrayList<>(), Message.NO_SUBSCRIPTION_PRICE_FOUND);
        return ApiResponse.success(response, Message.SUBSCRIPTION_PRICES_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPrice> getSubscriptionPriceById(String subscriptionPriceId) {
        SubscriptionPrice subscriptionPrice = findSubscriptionPriceByIdOrThrow(subscriptionPriceId);
        return ApiResponse.success(subscriptionPrice, Message.SUBSCRIPTION_PRICE_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPrice> addSubscriptionPrice(SubscriptionPriceRequest subscriptionPriceRequest) {
        validationUtil.validateAndThrow(subscriptionPriceRequest);
        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPriceRequest.getSubscriptionLength()).isPresent())
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, ErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPriceRequest.getSubscriptionLength().name()));

        SubscriptionPrice subscriptionPrice = SubscriptionPrice.builder()
                .subscriptionLength(subscriptionPriceRequest.getSubscriptionLength())
                .price(subscriptionPriceRequest.getPrice())
                .build();

        subscriptionPrice = subscriptionPriceRepository.save(subscriptionPrice);
        return ApiResponse.success(subscriptionPrice, Message.SUBSCRIPTION_PRICE_CREATED);
    }

    @Override
    public ApiResponse<SubscriptionPrice> updateSubscriptionPrice(SubscriptionPriceRequest subscriptionPriceRequest) {
        validationUtil.validateAndThrow(subscriptionPriceRequest);
        SubscriptionPrice subscriptionPrice = findSubscriptionPriceByIdOrThrow(subscriptionPriceRequest.getId());
        SubscriptionPrice possibleConflict = subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPriceRequest.getSubscriptionLength()).orElse(null);
        if (possibleConflict != null && !subscriptionPrice.getId().equals(possibleConflict.getId())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, Message.CREATE_FAILED, ErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPriceRequest.getSubscriptionLength().name()));
        }

        subscriptionPrice.setSubscriptionLength(subscriptionPriceRequest.getSubscriptionLength());
        subscriptionPrice.setPrice(subscriptionPriceRequest.getPrice());

        subscriptionPrice = subscriptionPriceRepository.save(subscriptionPrice);
        return ApiResponse.success(subscriptionPrice, Message.SUBSCRIPTION_PRICE_UPDATED);
    }

    @Override
    public ApiResponse<?> deleteSubscriptionPrice(String subscriptionId) {
        findSubscriptionPriceByIdOrThrow(subscriptionId);
        subscriptionPriceRepository.deleteById(subscriptionId);
        return ApiResponse.success(Message.SUBSCRIPTION_PRICE_DELETED);
    }

    @Override
    public ApiResponse<SubscriptionResponse> paySubscription(JwtClaim userInfo, SubscriptionRequest subscriptionRequest) {
        validationUtil.validateAndThrow(subscriptionRequest);
        WeddingOrganizer wo;
        if (userInfo != null) {
            wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
        } else {
            wo = weddingOrganizerService.loadWeddingOrganizerByEmail(subscriptionRequest.getEmail());
        }
        SubscriptionPrice price = findSubscriptionPriceByIdOrThrow(subscriptionRequest.getSubscriptionPriceId());

        Subscription subscription = Subscription.builder()
                .weddingOrganizer(wo)
                .subscriptionLength(price.getSubscriptionLength())
                .totalPaid(price.getPrice())
                .status(ESubscriptionPaymentStatus.PAID)
                .build();

        SubscriptionResponse response = SubscriptionResponse.all(subscription);
        return ApiResponse.success(response, Message.SUBSCRIPTION_PAID);
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> getOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<Subscription> subscriptionList = subscriptionRepository.findByWeddingOrganizerIdOrderByTransactionDate(wo.getId());
        Map<String, Integer> countByStatus = countByStatus(subscriptionList);
        if (subscriptionList == null || subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        subscriptionList = filterResult(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        countByStatus = countByStatus(subscriptionList);
        subscriptionList = filterByStatus(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.successSubscription(responses, pagingRequest, Message.SUBSCRIPTIONS_FOUND, countByStatus);
    }

    @Override
    public ApiResponse<SubscriptionResponse> confirmPaymentSubscriptionById(String subscriptionId) {
        if (subscriptionId == null || subscriptionId.isEmpty())
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_ID_IS_REQUIRED);
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_NOT_FOUND));

        subscription.setStatus(ESubscriptionPaymentStatus.CONFIRMED);
        subscription = subscriptionRepository.save(subscription);

        SubscriptionResponse response = SubscriptionResponse.all(subscription);
        return ApiResponse.success(response, Message.SUBSCRIPTION_PAYMENT_CONFIRMED);
    }

    @Override
    public ApiResponse<SubscriptionResponse> getSubscriptionById(JwtClaim userInfo, String subscriptionId) {
        try {
            if (subscriptionId == null || subscriptionId.isEmpty())
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_ID_IS_REQUIRED);
            Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, Message.FETCHING_FAILED, ErrorMessage.SUBSCRIPTION_NOT_FOUND));

            validateUserAccess(userInfo, subscription.getWeddingOrganizer());

            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, Message.SUBSCRIPTION_FOUND);
        } catch (AccessDeniedException e) {
            log.error("Access denied while loading subscription: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, Message.FETCHING_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> getAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest) {
        List<Subscription> subscriptionList = subscriptionRepository.findAllByOrderByTransactionDateDesc();
        Map<String, Integer> countByStatus = countByStatus(subscriptionList);
        if (subscriptionList == null || subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        subscriptionList = filterResult(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        countByStatus = countByStatus(subscriptionList);
        subscriptionList = filterByStatus(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscription(new ArrayList<>(), pagingRequest, Message.NO_SUBSCRIPTION_FOUND, countByStatus);

        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.successSubscription(responses, pagingRequest, Message.SUBSCRIPTIONS_FOUND, countByStatus);
    }
}
