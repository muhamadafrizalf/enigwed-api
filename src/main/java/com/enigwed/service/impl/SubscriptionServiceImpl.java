package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPacketRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionPackageResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.repository.SubscriptionPackageRepository;
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
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionServiceImpl implements SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;
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
        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.A_MONTH).isEmpty()) {
            SubscriptionPackage oneMonth = SubscriptionPackage.builder()
                    .name("Starter")
                    .subscriptionLength(ESubscriptionLength.A_MONTH)
                    .price(oneMonthPrice)
                    .build();
            subscriptionPackageRepository.save(oneMonth);
        }

        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.TWO_MONTHS).isEmpty()) {
            SubscriptionPackage twoMonths = SubscriptionPackage.builder()
                    .name("Basic")
                    .subscriptionLength(ESubscriptionLength.TWO_MONTHS)
                    .price(twoMonthsPrice)
                    .build();
            subscriptionPackageRepository.save(twoMonths);
        }

        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.THREE_MONTHS).isEmpty()) {
            SubscriptionPackage threeMonths = SubscriptionPackage.builder()
                    .name("Bronze")
                    .subscriptionLength(ESubscriptionLength.THREE_MONTHS)
                    .price(threeMonthsPrice)
                    .build();
            subscriptionPackageRepository.save(threeMonths);
        }

        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FOUR_MONTHS).isEmpty()) {
            SubscriptionPackage fourMonths = SubscriptionPackage.builder()
                    .name("Silver")
                    .subscriptionLength(ESubscriptionLength.FOUR_MONTHS)
                    .price(fourMonthsPrice)
                    .build();
            subscriptionPackageRepository.save(fourMonths);
        }

        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.FIVE_MONTHS).isEmpty()) {
            SubscriptionPackage fiveMonths = SubscriptionPackage.builder()
                    .name("Gold")
                    .subscriptionLength(ESubscriptionLength.FIVE_MONTHS)
                    .price(fiveMonthsPrice)
                    .build();
            subscriptionPackageRepository.save(fiveMonths);
        }

        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(ESubscriptionLength.SIX_MONTHS).isEmpty()) {
            SubscriptionPackage sixMonths = SubscriptionPackage.builder()
                    .name("Platinum")
                    .subscriptionLength(ESubscriptionLength.SIX_MONTHS)
                    .price(sixMonthsPrice)
                    .build();
            subscriptionPackageRepository.save(sixMonths);
        }
    }

    private SubscriptionPackage findSubscriptionPriceByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ID_IS_REQUIRED);
        return subscriptionPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_NOT_FOUND));
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
    public ApiResponse<List<SubscriptionPackageResponse>> findSubscriptionPackages() {
        List<SubscriptionPackage> subscriptionPackageList = subscriptionPackageRepository.findByDeletedAtIsNull();
        if (subscriptionPackageList == null || subscriptionPackageList.isEmpty())
            return ApiResponse.success(new ArrayList<>(), SMessage.NO_SUBSCRIPTION_PRICE_FOUND);
        List<Subscription> subscriptionList = getSubscriptions(LocalDateTime.now().minusMonths(12), LocalDateTime.now());
        Map<String, Long> map = new HashMap<>();
        for (Subscription subscription : subscriptionList) {
            map.put(subscription.getSubscriptionPackage().getId(), map.getOrDefault(subscription.getSubscriptionPackage().getId(), 0L) + 1);
        }
        List<SubscriptionPackageResponse> responses = subscriptionPackageList.stream()
                .map(subscriptionPackage -> SubscriptionPackageResponse.all(subscriptionPackage, map))
                .sorted(Comparator.comparingLong(SubscriptionPackageResponse::getOrderCount).reversed())
                .toList();
        for (int i = 0; i < Math.min(3, responses.size()); i++) {
            responses.get(i).setPopular(true);
        }
        responses = responses.stream()
                .sorted(Comparator.comparingInt(response -> response.getSubscriptionLength().getMonths()))
                .toList();
        return ApiResponse.success(responses, SMessage.SUBSCRIPTION_PRICES_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPackageResponse> findSubscriptionPackageById(String subscriptionPriceId) {
        SubscriptionPackage subscriptionPackage = findSubscriptionPriceByIdOrThrow(subscriptionPriceId);
        SubscriptionPackageResponse response = SubscriptionPackageResponse.simple(subscriptionPackage);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PRICE_FOUND);
    }

    @Override
    public ApiResponse<SubscriptionPackageResponse> createSubscriptionPackage(SubscriptionPacketRequest subscriptionPacketRequest) {
        validationUtil.validateAndThrow(subscriptionPacketRequest);
        if (subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPacketRequest.getSubscriptionLength()).isPresent())
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPacketRequest.getSubscriptionLength().name()));

        SubscriptionPackage subscriptionPackage = SubscriptionPackage.builder()
                .name(subscriptionPacketRequest.getName())
                .subscriptionLength(subscriptionPacketRequest.getSubscriptionLength())
                .price(subscriptionPacketRequest.getPrice())
                .build();

        subscriptionPackage = subscriptionPackageRepository.save(subscriptionPackage);

        SubscriptionPackageResponse response = SubscriptionPackageResponse.simple(subscriptionPackage);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PRICE_CREATED);
    }

    @Override
    public ApiResponse<SubscriptionPackageResponse> updateSubscriptionPackage(SubscriptionPacketRequest subscriptionPacketRequest) {
        validationUtil.validateAndThrow(subscriptionPacketRequest);
        SubscriptionPackage subscriptionPackage = findSubscriptionPriceByIdOrThrow(subscriptionPacketRequest.getId());
        SubscriptionPackage possibleConflict = subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPacketRequest.getSubscriptionLength()).orElse(null);
        if (possibleConflict != null && !subscriptionPackage.getId().equals(possibleConflict.getId())) {
            throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, SErrorMessage.SUBSCRIPTION_PRICE_ALREADY_EXIST(subscriptionPacketRequest.getSubscriptionLength().name()));
        }

        subscriptionPackage.setSubscriptionLength(subscriptionPacketRequest.getSubscriptionLength());
        subscriptionPackage.setPrice(subscriptionPacketRequest.getPrice());

        subscriptionPackage = subscriptionPackageRepository.save(subscriptionPackage);

        SubscriptionPackageResponse response = SubscriptionPackageResponse.simple(subscriptionPackage);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PRICE_UPDATED);
    }

    @Override
    public ApiResponse<?> deleteSubscriptionPackage(String subscriptionId) {
        SubscriptionPackage packet = findSubscriptionPriceByIdOrThrow(subscriptionId);
        packet.setDeletedAt(LocalDateTime.now());
        subscriptionPackageRepository.save(packet);
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
        SubscriptionPackage packet = findSubscriptionPriceByIdOrThrow(subscriptionRequest.getSubscriptionPriceId());

        Image paymentImage = imageService.createImage(subscriptionRequest.getPaymentImage());

        Subscription subscription = Subscription.builder()
                .weddingOrganizer(wo)
                .subscriptionPackage(packet)
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
        LocalDateTime activeUntil = activeFrom.plusMonths(subscription.getSubscriptionPackage().getSubscriptionLength().getMonths());

        subscription.setActiveFrom(activeFrom);
        subscription.setActiveUntil(activeUntil);
        subscription = subscriptionRepository.saveAndFlush(subscription);

        weddingOrganizerService.extendWeddingOrganizerSubscription(subscription.getWeddingOrganizer(), subscription.getSubscriptionPackage());

        /* SEND NOTIFICATION */
        sendNotificationWeddingOrganizer(ENotificationType.SUBSCRIPTION_CONFIRMED, subscription, SMessage.SUBSCRIPTION_CONFIRMED(subscription.getSubscriptionPackage().getName()));

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
