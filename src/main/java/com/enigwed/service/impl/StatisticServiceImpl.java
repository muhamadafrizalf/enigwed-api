package com.enigwed.service.impl;

import com.enigwed.constant.ERole;
import com.enigwed.constant.ErrorMessage;
import com.enigwed.constant.Message;
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

    @Override
    public ApiResponse<StatisticResponse> getStatisticsIncome(JwtClaim userInfo, LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) throw new ErrorResponse(HttpStatus.BAD_REQUEST, Message.FETCHING_FAILED, ErrorMessage.INVALID_DATE);
        if (userInfo.getRole().equals(ERole.ROLE_WO.name())) {
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderService.loadAllOrders(wo.getId(), from, to);
            Map<String, Double> statistic = getStatisticOrder(orderList, from, to);
            StatisticResponse response = StatisticResponse.wo(wo, statistic);
            return ApiResponse.success(response, Message.STATISTIC_FETCHED);
        } else {
            List<Subscription> subscriptionList = subscriptionService.getSubscriptions(from, to);
            Map<String, Double> statistic = getStatisticSubscription(subscriptionList, from, to);
            StatisticResponse response = StatisticResponse.admin(statistic);
            return ApiResponse.success(response, Message.STATISTIC_FETCHED);
        }
    }
}
