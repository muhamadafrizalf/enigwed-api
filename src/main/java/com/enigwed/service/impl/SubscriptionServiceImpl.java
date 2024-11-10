package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPacketRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.SubscriptionPriceRepository;
import com.enigwed.repository.SubscriptionRepository;
import com.enigwed.service.*;
import com.enigwed.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
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
    private final ImageService imageService;
    private final NotificationService notificationService;
    private final UserCredentialService userCredentialService;
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
            SubscriptionPacket oneMonth = SubscriptionPacket.builder()
                    .name("Starter")
                    .subscriptionLength(ESubscriptionLength.A_MONTH)
                    .price(oneMonthPrice)
                    .build();
            subscriptionPriceRepository.save(oneMonth);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.TWO_MONTHS).isEmpty()) {
            SubscriptionPacket twoMonths = SubscriptionPacket.builder()
                    .name("Basic")
                    .subscriptionLength(ESubscriptionLength.TWO_MONTHS)
                    .price(twoMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(twoMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.THREE_MONTHS).isEmpty()) {
            SubscriptionPacket threeMonths = SubscriptionPacket.builder()
                    .name("Bronze")
                    .subscriptionLength(ESubscriptionLength.THREE_MONTHS)
                    .price(threeMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(threeMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FOUR_MONTHS).isEmpty()) {
            SubscriptionPacket fourMonths = SubscriptionPacket.builder()
                    .name("Silver")
                    .subscriptionLength(ESubscriptionLength.FOUR_MONTHS)
                    .price(fourMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(fourMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FIVE_MONTHS).isEmpty()) {
            SubscriptionPacket fiveMonths = SubscriptionPacket.builder()
                    .name("Gold")
                    .subscriptionLength(ESubscriptionLength.FIVE_MONTHS)
                    .price(fiveMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(fiveMonths);
        }

        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.SIX_MONTHS).isEmpty()) {
            SubscriptionPacket sixMonths = SubscriptionPacket.builder()
                    .name("Platinum")
                    .subscriptionLength(ESubscriptionLength.SIX_MONTHS)
                    .price(sixMonthsPrice)
                    .build();
            subscriptionPriceRepository.save(sixMonths);
        }
    }

    private SubscriptionPacket findSubscriptionPriceByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ID_IS_REQUIRED);
        return subscriptionPriceRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_NOT_FOUND));
    }

    private List<Subscription> filterResult(FilterRequest filter, List<Subscription> list) {
        return list.stream()
                .filter(item ->
                        (filter.getStartDate() == null || !item.getTransactionDate().isBefore(filter.getStartDate())) &&
                        (filter.getEndDate() == null || !item.getTransactionDate().isAfter(filter.getEndDate()))
                )
                .toList();
    }

    private void sendNotificationWeddingOrganizer(ENotificationType type, Subscription subscription, String message) {
        Notification notification = Notification.builder()
                .channel(ENotificationChannel.SYSTEM)
                .type(type)
                .receiver(EReceiver.WEDDING_ORGANIZER)
                .receiverId(subscription.getWeddingOrganizer().getUserCredential().getId())
                .dataType(EDataType.SUBSCRIPTION)
                .dataId(subscription.getId())
                .message(message)
                .build();
        notificationService.createNotification(notification);
        /*

            Create notification for channel email

        */
    }

    private void sendNotificationAdmin(ENotificationType type, Subscription subscription, String message) {
        Notification notification = Notification.builder()
                .channel(ENotificationChannel.SYSTEM)
                .type(type)
                .receiver(EReceiver.ADMIN)
                .receiverId(userCredentialService.loadAdminId())
                .dataType(EDataType.SUBSCRIPTION)
                .dataId(subscription.getId())
                .message(message)
                .build();
        notificationService.createNotification(notification);
        /*

            Create notification for channel email

        */
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
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

    @Override
    public List<Subscription> getSubscriptions(LocalDateTime from, LocalDateTime to) {
        return subscriptionRepository.findByStatusAndTransactionDateBetween(ESubscriptionPaymentStatus.CONFIRMED, from, to);
    }

    @Override
    public ApiResponse<List<SubscriptionPacket>> getSubscriptionPrices() {
        List<SubscriptionPacket> response = subscriptionPriceRepository.findByDeletedAtIsNull();
        if (response == null || response.isEmpty())
            return ApiResponse.success(new ArrayList<>(), SMessage.NO_SUBSCRIPTION_PRICE_FOUND);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PRICES_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPacket> getSubscriptionPriceById(String subscriptionPriceId) {
        SubscriptionPacket subscriptionPrice = findSubscriptionPriceByIdOrThrow(subscriptionPriceId);
        return ApiResponse.success(subscriptionPrice, SMessage.SUBSCRIPTION_PRICE_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPacket> addSubscriptionPrice(SubscriptionPacketRequest subscriptionPacketRequest) {
        validationUtil.validateAndThrow(subscriptionPacketRequest);
        if (subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPacketRequest.getSubscriptionLength()).isPresent())
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPacketRequest.getSubscriptionLength().name()));

        SubscriptionPacket subscriptionPrice = SubscriptionPacket.builder()
                .subscriptionLength(subscriptionPacketRequest.getSubscriptionLength())
                .price(subscriptionPacketRequest.getPrice())
                .build();

        subscriptionPrice = subscriptionPriceRepository.save(subscriptionPrice);
        return ApiResponse.success(subscriptionPrice, SMessage.SUBSCRIPTION_PRICE_CREATED);
    }

    @Override
    public ApiResponse<SubscriptionPacket> updateSubscriptionPrice(SubscriptionPacketRequest subscriptionPacketRequest) {
        validationUtil.validateAndThrow(subscriptionPacketRequest);
        SubscriptionPacket subscriptionPacket = findSubscriptionPriceByIdOrThrow(subscriptionPacketRequest.getId());
        SubscriptionPacket possibleConflict = subscriptionPriceRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPacketRequest.getSubscriptionLength()).orElse(null);
        if (possibleConflict != null && !subscriptionPacket.getId().equals(possibleConflict.getId())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPacketRequest.getSubscriptionLength().name()));
        }

        subscriptionPacket.setSubscriptionLength(subscriptionPacketRequest.getSubscriptionLength());
        subscriptionPacket.setPrice(subscriptionPacketRequest.getPrice());

        subscriptionPacket = subscriptionPriceRepository.save(subscriptionPacket);
        return ApiResponse.success(subscriptionPacket, SMessage.SUBSCRIPTION_PRICE_UPDATED);
    }

    @Override
    public ApiResponse<?> deleteSubscriptionPrice(String subscriptionId) {
        SubscriptionPacket packet = findSubscriptionPriceByIdOrThrow(subscriptionId);
        packet.setDeletedAt(LocalDateTime.now());
        subscriptionPriceRepository.save(packet);
        return ApiResponse.success(SMessage.SUBSCRIPTION_PRICE_DELETED);
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
        SubscriptionPacket packet = findSubscriptionPriceByIdOrThrow(subscriptionRequest.getSubscriptionPriceId());

        Image paymentImage = imageService.createImage(subscriptionRequest.getPaymentImage());

        Subscription subscription = Subscription.builder()
                .weddingOrganizer(wo)
                .subscriptionPacket(packet)
                .totalPaid(packet.getPrice())
                .status(ESubscriptionPaymentStatus.PAID)
                .paymentImage(paymentImage)
                .build();

        subscription = subscriptionRepository.save(subscription);

        /* CREATE NOTIFICATION */
        sendNotificationAdmin(ENotificationType.SUBSCRIPTION_RECEIVED, subscription, SMessage.NEW_SUBSCRIPTION_RECEIVED(wo.getName()));

        SubscriptionResponse response = SubscriptionResponse.all(subscription);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PAID);
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> getOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<Subscription> subscriptionList = subscriptionRepository.findByWeddingOrganizerIdOrderByTransactionDate(wo.getId());
        Map<String, Integer> countByStatus = countByStatus(subscriptionList);
        if (subscriptionList == null || subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        subscriptionList = filterResult(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        countByStatus = countByStatus(subscriptionList);
        subscriptionList = filterByStatus(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.successSubscriptionList(responses, pagingRequest, SMessage.SUBSCRIPTIONS_FOUND, countByStatus);
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> getActiveSubscriptions(JwtClaim userInfo, PagingRequest pagingRequest) {
        WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

        List<Subscription> subscriptionList = subscriptionRepository.findByWeddingOrganizerIdOrderByTransactionDate(wo.getId());
        subscriptionList = subscriptionList.stream()
                .filter(subscription -> subscription.getStatus().equals(ESubscriptionPaymentStatus.CONFIRMED))
                .filter(subscription -> subscription.getActiveUntil().isAfter(LocalDateTime.now()))
                .toList();

        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.SUBSCRIPTIONS_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionResponse> confirmPaymentSubscriptionById(String subscriptionId) {
        if (subscriptionId == null || subscriptionId.isEmpty())
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_ID_IS_REQUIRED);
        Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_NOT_FOUND));

        subscription.setStatus(ESubscriptionPaymentStatus.CONFIRMED);
        LocalDateTime activeFrom = subscription.getWeddingOrganizer().getUserCredential().getActiveUntil();
        LocalDateTime activeUntil = activeFrom.plusMonths(subscription.getSubscriptionPacket().getSubscriptionLength().getMonths());

        subscription.setActiveFrom(activeFrom);
        subscription.setActiveUntil(activeUntil);
        subscription = subscriptionRepository.saveAndFlush(subscription);

        weddingOrganizerService.extendWeddingOrganizerSubscription(subscription.getWeddingOrganizer(), subscription.getSubscriptionPacket());

        /* SEND NOTIFICATION */
        sendNotificationWeddingOrganizer(ENotificationType.SUBSCRIPTION_CONFIRMED, subscription, SMessage.SUBSCRIPTION_CONFIRMED(subscription.getSubscriptionPacket().getName()));

        SubscriptionResponse response = SubscriptionResponse.all(subscription);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PAYMENT_CONFIRMED);
    }

    @Override
    public ApiResponse<SubscriptionResponse> getSubscriptionById(JwtClaim userInfo, String subscriptionId) {
        try {
            if (subscriptionId == null || subscriptionId.isEmpty())
                throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_ID_IS_REQUIRED);
            Subscription subscription = subscriptionRepository.findById(subscriptionId).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_NOT_FOUND));

            validateUserAccess(userInfo, subscription.getWeddingOrganizer());

            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_FOUND);
        } catch (AccessDeniedException e) {
            log.error("Access denied while loading subscription: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.FETCHING_FAILED, e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> getAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest) {
        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        Map<String, Integer> countByStatus = countByStatus(subscriptionList);
        if (subscriptionList == null || subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        subscriptionList = filterResult(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        countByStatus = countByStatus(subscriptionList);
        subscriptionList = filterByStatus(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.successSubscriptionList(responses, pagingRequest, SMessage.SUBSCRIPTIONS_FOUND, countByStatus);
    }
}
