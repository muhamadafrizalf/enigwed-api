package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.request.FilterRequest;
import com.enigwed.dto.request.PagingRequest;
import com.enigwed.dto.request.SubscriptionPackageRequest;
import com.enigwed.dto.request.SubscriptionRequest;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.SubscriptionPackageResponse;
import com.enigwed.dto.response.SubscriptionResponse;
import com.enigwed.entity.*;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.exception.ValidationException;
import com.enigwed.repository.SubscriptionPackageRepository;
import com.enigwed.repository.SubscriptionRepository;
import com.enigwed.service.*;
import com.enigwed.util.AccessValidationUtil;
import com.enigwed.util.StatisticUtil;
import com.enigwed.util.ValidationUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StatisticUtil statisticUtil;
    private final AccessValidationUtil accessValidationUtil;

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

    private SubscriptionPackage findSubscriptionPackageByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PACKAGE_ID_IS_REQUIRED);
        return subscriptionPackageRepository.findByIdAndDeletedAtIsNull(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_PACKAGE_NOT_FOUND(id)));
    }

    private Subscription findSubscriptionByIdOrThrow(String id) {
        if (id == null || id.isEmpty()) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_ID_IS_REQUIRED);
        return subscriptionRepository.findById(id).orElseThrow(() -> new ErrorResponse(HttpStatus.NOT_FOUND, SMessage.FETCHING_FAILED, SErrorMessage.SUBSCRIPTION_NOT_FOUND(id)));
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
                .filter(item -> filter.getSubscriptionPaymentStatus() == null || item.getStatus().equals(filter.getSubscriptionPaymentStatus()))
                .toList();
    }

    private ApiResponse<List<SubscriptionResponse>> getListApiResponse(PagingRequest pagingRequest, FilterRequest filterRequest, List<Subscription> subscriptionList) {
        /* COUNT SUBSCRIPTION BY STATUS */
        Map<String, Integer> countByStatus = statisticUtil.countBySubscriptionPaymentStatus(subscriptionList);
        if (subscriptionList == null || subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        /* FILTER RESULT */
        subscriptionList = filterResult(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        /* RE-COUNT SUBSCRIPTION BY STATUS */
        countByStatus = statisticUtil.countBySubscriptionPaymentStatus(subscriptionList);

        /* FILTER RESULT BY STATUS */
        subscriptionList = filterByStatus(filterRequest, subscriptionList);
        if (subscriptionList.isEmpty())
            return ApiResponse.successSubscriptionList(new ArrayList<>(), pagingRequest, SMessage.NO_SUBSCRIPTION_FOUND, countByStatus);

        /* MAP RESPONSE */
        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.successSubscriptionList(responses, pagingRequest, SMessage.SUBSCRIPTIONS_FOUND, countByStatus);
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

    private void validateUserAccess(JwtClaim userInfo, WeddingOrganizer weddingOrganizer) throws AccessDeniedException {
        String userCredentialId = weddingOrganizer.getUserCredential().getId();
        if (userInfo.getUserId().equals(userCredentialId)) {
            return;
        } else if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            return;
        }
        throw new AccessDeniedException(SErrorMessage.ACCESS_DENIED);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Subscription> getSubscriptions(LocalDateTime from, LocalDateTime to) {
        return subscriptionRepository.findByStatusAndTransactionDateBetween(ESubscriptionPaymentStatus.CONFIRMED, from, to);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<SubscriptionPackageResponse>> findSubscriptionPackages() {
        Sort sort = Sort.by(Sort.Order.asc("price"));
        List<SubscriptionPackage> subscriptionPackageList = subscriptionPackageRepository.findByDeletedAtIsNull(sort);
        if (subscriptionPackageList == null || subscriptionPackageList.isEmpty())
            return ApiResponse.success(new ArrayList<>(), SMessage.NO_SUBSCRIPTION_PACKAGE_FOUND);
        List<SubscriptionPackageResponse> responses = subscriptionPackageList.stream().map(SubscriptionPackageResponse::all).toList();
        return ApiResponse.success(responses, SMessage.SUBSCRIPTION_PACKAGES_FOUND(subscriptionPackageList.size()));
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<SubscriptionPackageResponse> findSubscriptionPackageById(String subscriptionPackageId) {
        SubscriptionPackage subscriptionPackage = findSubscriptionPackageByIdOrThrow(subscriptionPackageId);
        SubscriptionPackageResponse response = SubscriptionPackageResponse.all(subscriptionPackage);
        return ApiResponse.success(response, SMessage.SUBSCRIPTION_PACKAGE_FOUND(subscriptionPackageId));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SubscriptionPackageResponse> createSubscriptionPackage(SubscriptionPackageRequest subscriptionPackageRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(subscriptionPackageRequest);

            /* CHECK DATA INTEGRITY */
            // ERROR RESPONSE //
            SubscriptionPackage possibleConflict = subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPackageRequest.getSubscriptionLength()).orElse(null);
            if (possibleConflict != null)
                throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.CREATE_FAILED, SErrorMessage.SUBSCRIPTION_PACKAGE_ALREADY_EXIST(subscriptionPackageRequest.getSubscriptionLength().name()));

            /* CREATE SUBSCRIPTION PACKAGE */
            SubscriptionPackage subscriptionPackage = SubscriptionPackage.builder()
                    .name(subscriptionPackageRequest.getName())
                    .subscriptionLength(subscriptionPackageRequest.getSubscriptionLength())
                    .price(subscriptionPackageRequest.getPrice())
                    .popular(subscriptionPackageRequest.getPopular() != null ? subscriptionPackageRequest.getPopular() : false)
                    .build();

            /* SAVE SUBSCRIPTION PACKAGE */
            subscriptionPackage = subscriptionPackageRepository.save(subscriptionPackage);

            /* MAP RESPONSE */
            SubscriptionPackageResponse response = SubscriptionPackageResponse.all(subscriptionPackage);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_PRICE_CREATED);

        } catch (ValidationException e) {
            log.error("Validation error while creating subscription package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        }  catch (ErrorResponse e) {
            log.error("Error while creating subscription package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SubscriptionPackageResponse> updateSubscriptionPackage(SubscriptionPackageRequest subscriptionPackageRequest) {
        try {
            /* LOAD SUBSCRIPTION PACKAGE */
            // ErrorResponse //
            SubscriptionPackage subscriptionPackage = findSubscriptionPackageByIdOrThrow(subscriptionPackageRequest.getId());

            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(subscriptionPackageRequest);

            /* CHECK DATA INTEGRITY */
            // ERROR RESPONSE //
            SubscriptionPackage possibleConflict = subscriptionPackageRepository.findBySubscriptionLengthAndDeletedAtIsNull(subscriptionPackageRequest.getSubscriptionLength()).orElse(null);
            if (possibleConflict != null && !subscriptionPackage.getId().equals(possibleConflict.getId()))
                throw new ErrorResponse(HttpStatus.CONFLICT, SMessage.UPDATE_FAILED, SErrorMessage.SUBSCRIPTION_PACKAGE_ALREADY_EXIST(subscriptionPackageRequest.getSubscriptionLength().name()));

            /* UPDATE SUBSCRIPTION PACKAGE */
            subscriptionPackage.setSubscriptionLength(subscriptionPackageRequest.getSubscriptionLength());
            subscriptionPackage.setPrice(subscriptionPackageRequest.getPrice());
            if (subscriptionPackageRequest.getPopular() != null) subscriptionPackage.setPopular(subscriptionPackageRequest.getPopular());

            /* SAVE SUBSCRIPTION PACKAGE */
            subscriptionPackage = subscriptionPackageRepository.save(subscriptionPackage);

            /* MAP RESPONSE */
            SubscriptionPackageResponse response = SubscriptionPackageResponse.all(subscriptionPackage);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_PACKAGE_UPDATED(subscriptionPackage.getId()));

        } catch (ValidationException e) {
            log.error("Validation error while updating subscription package: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.UPDATE_FAILED, e.getErrors().get(0));
        } catch (ErrorResponse e) {
            log.error("Error while updating subscription package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<?> deleteSubscriptionPackage(String subscriptionId) {
        try {
            /* LOAD SUBSCRIPTION PACKAGE */
            // ErrorResponse //
            SubscriptionPackage subscriptionPackage = findSubscriptionPackageByIdOrThrow(subscriptionId);

            /* SET DELETED AT*/
            subscriptionPackage.setDeletedAt(LocalDateTime.now());

            /* SAVE SUBSCRIPTION PACKAGE */
            subscriptionPackageRepository.save(subscriptionPackage);

            return ApiResponse.success(SMessage.SUBSCRIPTION_PACKAGE_DELETED(subscriptionId));
        } catch (ErrorResponse e) {
            log.error("Error while deleting subscription package: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<SubscriptionResponse>> findOwnSubscriptions(JwtClaim userInfo, FilterRequest filterRequest, PagingRequest pagingRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND SUBSCRIPTIONS */
            // ErrorResponse //
            List<Subscription> subscriptionList = subscriptionRepository.findByWeddingOrganizerIdOrderByTransactionDateDesc(wo.getId());

            /* FILTER AND MAP RESPONSE */
            return getListApiResponse(pagingRequest, filterRequest, subscriptionList);
        } catch (ErrorResponse e) {
            log.error("Error while finding own subscriptions: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<SubscriptionResponse>> findOwnActiveSubscriptions(JwtClaim userInfo, PagingRequest pagingRequest) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND SUBSCRIPTIONS */
            // ErrorResponse //
            List<Subscription> subscriptionList = subscriptionRepository.findByWeddingOrganizerIdOrderByTransactionDateDesc(wo.getId());

            /* FILTER ACTIVE SUBSCRIPTIONS */
            subscriptionList = subscriptionList.stream()
                    .filter(subscription -> subscription.getStatus().equals(ESubscriptionPaymentStatus.CONFIRMED))
                    .filter(subscription -> subscription.getActiveUntil() != null && subscription.getActiveUntil().isAfter(LocalDateTime.now()))
                    .toList();

            if(subscriptionList.isEmpty()) {
                return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_ACTIVE_SUBSCRIPTION_FOUND(wo.getName()));
            }

            /* MAP RESPONSE */
            List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
            return ApiResponse.success(responses, pagingRequest, SMessage.ACTIVE_SUBSCRIPTIONS_FOUND(responses.size(), wo.getName()));

        } catch (ErrorResponse e) {
            log.error("Error while finding active own subscriptions: {}", e.getError());
            throw e;
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<SubscriptionResponse> findOwnSubscriptionById(JwtClaim userInfo, String subscriptionId) {
        try {
            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());

            /* FIND SUBSCRIPTION */
            // ErrorResponse //
            Subscription subscription = findSubscriptionByIdOrThrow(subscriptionId);

            /* VALIDATE ACCESS */
            // AccessDeniedException //
            accessValidationUtil.validateUser(userInfo, subscription.getWeddingOrganizer());

            /* MAP SUBSCRIPTION */
            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_FOUND(subscriptionId));

        } catch (AccessDeniedException e) {
            log.error("Access denied while finding own subscription by ID: {}", e.getMessage());
            throw new ErrorResponse(HttpStatus.FORBIDDEN, SMessage.FETCHING_FAILED, e.getMessage());
        }  catch (ErrorResponse e) {
            log.error("Error while finding own subscription by ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SubscriptionResponse> paySubscription(JwtClaim userInfo, SubscriptionRequest subscriptionRequest) {
        try {
            /* VALIDATE INPUT */
            // ValidationException //
            validationUtil.validateAndThrow(subscriptionRequest);

            /* LOAD WEDDING ORGANIZER */
            // ErrorResponse //
            WeddingOrganizer wo;
            if (userInfo != null) {
                wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            } else {
                wo = weddingOrganizerService.loadWeddingOrganizerByEmail(subscriptionRequest.getEmail());
            }

            /* LOAD SUBSCRIPTION PACKAGE */
            SubscriptionPackage subscriptionPackage = findSubscriptionPackageByIdOrThrow(subscriptionRequest.getSubscriptionPriceId());

            /* SAVE PAYMENT IMAGE */
            Image paymentImage = imageService.createImage(subscriptionRequest.getPaymentImage());

            /* CREATE SUBSCRIPTION */
            Subscription subscription = Subscription.builder()
                    .weddingOrganizer(wo)
                    .subscriptionPackage(subscriptionPackage)
                    .totalPaid(subscriptionPackage.getPrice())
                    .status(ESubscriptionPaymentStatus.PAID)
                    .paymentImage(paymentImage)
                    .build();

            /* SAVE SUBSCRIPTION */
            subscription = subscriptionRepository.save(subscription);

            /* CREATE NOTIFICATION */
            sendNotificationAdmin(ENotificationType.SUBSCRIPTION_RECEIVED, subscription, SNotificationMessage.NEW_SUBSCRIPTION_RECEIVED(wo.getName()));

            /* MAP RESPONSE */
            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_PAID(subscription.getId()));

        } catch (ValidationException e) {
            log.error("Validation error while creating subscription: {}", e.getErrors());
            throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.CREATE_FAILED, e.getErrors().get(0));
        }
    }

    @Override
    public ApiResponse<List<SubscriptionResponse>> findAllSubscriptions(PagingRequest pagingRequest, FilterRequest filterRequest) {
        /* FIND ALL SUBSCRIPTIONS */
        List<Subscription> subscriptionList = subscriptionRepository.findAll();

        /* FILTER AND MAP RESPONSE */
        return getListApiResponse(pagingRequest, filterRequest, subscriptionList);
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<List<SubscriptionResponse>> findAllActiveSubscriptions(PagingRequest pagingRequest, String weddingOrganizerId) {
        /* FIND ALL SUBSCRIPTIONS */
        List<Subscription> subscriptionList;
        if(weddingOrganizerId == null || weddingOrganizerId.isEmpty()) {
            subscriptionList = subscriptionRepository.findAll();
        } else {
            subscriptionList = subscriptionRepository.findByWeddingOrganizerId(weddingOrganizerId);
        }

        /* FILTER ACTIVE SUBSCRIPTIONS */
        subscriptionList = subscriptionList.stream()
                .filter(subscription -> subscription.getStatus().equals(ESubscriptionPaymentStatus.CONFIRMED))
                .filter(subscription -> subscription.getActiveUntil() != null && subscription.getActiveUntil().isAfter(LocalDateTime.now()))
                .toList();

        if(subscriptionList.isEmpty()) {
            return ApiResponse.success(new ArrayList<>(), pagingRequest, SMessage.NO_ACTIVE_SUBSCRIPTION_FOUND);
        }

        /* MAP RESPONSE */
        List<SubscriptionResponse> responses = subscriptionList.stream().map(SubscriptionResponse::all).toList();
        return ApiResponse.success(responses, pagingRequest, SMessage.ACTIVE_SUBSCRIPTIONS_FOUND(responses.size()));
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<SubscriptionResponse> findSubscriptionById(String subscriptionId) {
        try {
            /* FIND SUBSCRIPTION */
            // ErrorResponse //
            Subscription subscription = findSubscriptionByIdOrThrow(subscriptionId);

            /* MAP RESPONSE */
            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_FOUND(subscriptionId));
        } catch (ErrorResponse e) {
            log.error("Error while finding subscription by ID: {}", e.getError());
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SubscriptionResponse> confirmSubscriptionPaymentById(String subscriptionId) {
        try {
            /* LOAD SUBSCRIPTION */
            // ErrorResponse //
            Subscription subscription = findSubscriptionByIdOrThrow(subscriptionId);

            /* SET ACTIVE FROM AND ACTIVE UNTIL */
            subscription.setStatus(ESubscriptionPaymentStatus.CONFIRMED);
            LocalDateTime activeFrom = subscription.getWeddingOrganizer().getUserCredential().getActiveUntil();
            LocalDateTime activeUntil = activeFrom.plusMonths(subscription.getSubscriptionPackage().getSubscriptionLength().getMonths());

            subscription.setActiveFrom(activeFrom);
            subscription.setActiveUntil(activeUntil);
            subscription = subscriptionRepository.saveAndFlush(subscription);

            /* EXTEND USER ACTIVE UNTIL */
            weddingOrganizerService.extendWeddingOrganizerSubscription(subscription.getWeddingOrganizer(), subscription.getSubscriptionPackage());

            /* SEND NOTIFICATION */
            sendNotificationWeddingOrganizer(ENotificationType.SUBSCRIPTION_CONFIRMED, subscription, SNotificationMessage.SUBSCRIPTION_CONFIRMED(subscription.getSubscriptionPackage().getName()));

            /* MAP RESPONSE */
            SubscriptionResponse response = SubscriptionResponse.all(subscription);
            return ApiResponse.success(response, SMessage.SUBSCRIPTION_CONFIRMED(subscriptionId));
        } catch (ErrorResponse e) {
            log.error("Error while confirming subscription: {}", e.getError());
            throw e;
        }
    }
}
