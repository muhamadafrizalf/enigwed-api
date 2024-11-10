package com.enigwed.service.impl;

import com.enigwed.constant.*;
import com.enigwed.dto.JwtClaim;
import com.enigwed.dto.response.ApiResponse;
import com.enigwed.dto.response.StatisticResponse;
import com.enigwed.entity.Order;
import com.enigwed.entity.Subscription;
import com.enigwed.entity.WeddingOrganizer;
import com.enigwed.exception.ErrorResponse;
import com.enigwed.service.OrderService;
import com.enigwed.service.StatisticService;
import com.enigwed.service.SubscriptionService;
import com.enigwed.service.WeddingOrganizerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticServiceImpl implements StatisticService {
    private final WeddingOrganizerService weddingOrganizerService;
    private final OrderService orderService;
    private final SubscriptionService subscriptionService;

    private Map<String, Double> getStatisticOrder(List<Order> list, LocalDateTime from, LocalDateTime to) {
        Map<String, Double> map = new HashMap<>();
        LocalDateTime current = from;
        while (!current.isAfter(to)) {
            // Format the key as "YYYY-MM"
            String yearMonthKey = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            map.put(yearMonthKey, 0.0);
            current = current.plusMonths(1);
        }

        for (Order order : list) {
            String transDateKey = order.getTransactionDate().getYear() + "-" +
                    String.format("%02d", order.getTransactionDate().getMonthValue());
            map.put(transDateKey, map.getOrDefault(transDateKey, 0.0) + order.getTotalPrice());
        }

        return map;
    }

    Map<String, Double> getStatisticSubscription(List<Subscription> list, LocalDateTime from, LocalDateTime to) {
        Map<String, Double> map = new HashMap<>();
        LocalDateTime current = from;
        while (!current.isAfter(to)) {
            // Format the key as "YYYY-MM"
            String yearMonthKey = current.getYear() + "-" + String.format("%02d", current.getMonthValue());
            map.put(yearMonthKey, 0.0);
            current = current.plusMonths(1);
        }

        for (Subscription subscription : list) {
            String transDateKey = subscription.getTransactionDate().getYear() + "-" +
                    String.format("%02d", subscription.getTransactionDate().getMonthValue());
            map.put(transDateKey, map.getOrDefault(transDateKey, 0.0) + subscription.getTotalPaid());
        }

        return map;
    }

    private EUserStatus getUserStatus(WeddingOrganizer wo) {
        if (wo.getUserCredential().isActive()) {
            return EUserStatus.ACTIVE;
        } else if (wo.getDeletedAt() == null) {
            return EUserStatus.INACTIVE;
        } else {
            return EUserStatus.DELETED;
        }
    }

    private Map<String, Integer> countWeddingOrganizerByStatus(List<WeddingOrganizer> woList) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (EUserStatus status : EUserStatus.values()) {
            map.put(status.name(), 0);
        }

        for (WeddingOrganizer wo : woList) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(getUserStatus(wo).name(), map.get(getUserStatus(wo).name()) + 1);
        }
        return map;
    }

    private Map<String, Integer> countOrderByStatus(List<Order> orderList) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (EStatus status : EStatus.values()) {
            map.put(status.name(), 0);
        }
        for (Order order : orderList) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(order.getStatus().name(), map.get(order.getStatus().name()) + 1);
        }
        return map;
    }

    @Override
    public ApiResponse<StatisticResponse> getStatisticsIncome(JwtClaim userInfo, LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.INVALID_DATE);
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderService.loadAllOrders(wo.getId(), from, to);
            Map<String, Double> statistic = getStatisticOrder(orderList, from, to);
            Map<String, Integer> countByStatus = countOrderByStatus(orderList);
            StatisticResponse response = StatisticResponse.wo(wo, countByStatus, statistic);
            return ApiResponse.success(response, SMessage.STATISTIC_FETCHED);
        } else {
            List<Subscription> subscriptionList = subscriptionService.getSubscriptions(from, to);
            Map<String, Double> statistic = getStatisticSubscription(subscriptionList, from, to);
            List<WeddingOrganizer> woList = weddingOrganizerService.findAllWeddingOrganizers();
            Map<String, Integer> countByStatus = countWeddingOrganizerByStatus(woList);
            StatisticResponse response = StatisticResponse.admin(woList, countByStatus, statistic);
            return ApiResponse.success(response, SMessage.STATISTIC_FETCHED);
        }
    }
}
