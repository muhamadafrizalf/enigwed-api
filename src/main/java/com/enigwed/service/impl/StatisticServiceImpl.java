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
import com.enigwed.util.StatisticUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final StatisticUtil statisticUtil;

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

    @Transactional(readOnly = true)
    @Override
    public ApiResponse<StatisticResponse> getStatisticsIncome(JwtClaim userInfo, LocalDateTime from, LocalDateTime to) {
        if (from.isAfter(to)) throw new ErrorResponse(HttpStatus.BAD_REQUEST, SMessage.FETCHING_FAILED, SErrorMessage.INVALID_DATE);
        if (userInfo.getRole().equals(ERole.ROLE_ADMIN.name())) {
            List<Subscription> subscriptionList = subscriptionService.loadConfirmedSubscription(from, to);
            Map<String, Double> statistic = getStatisticSubscription(subscriptionList, from, to);
            List<WeddingOrganizer> woList = weddingOrganizerService.findAllWeddingOrganizers();
            Map<String, Integer> countByStatus = statisticUtil.countWeddingOrganizerByStatus(woList);
            StatisticResponse response = StatisticResponse.admin(woList, countByStatus, statistic);
            return ApiResponse.success(response, SMessage.STATISTIC_FETCHED);
        } else {
            WeddingOrganizer wo = weddingOrganizerService.loadWeddingOrganizerByUserCredentialId(userInfo.getUserId());
            List<Order> orderList = orderService.loadOrdersByWeddingOrganizerIdAndTransactionDateBetween(wo.getId(), from, to);
            Map<String, Integer> countByStatus = statisticUtil.countOrderByStatus(orderList);
            List<Order> finishedOrderList = orderList.stream().filter(order -> order.getStatus().equals(EStatus.FINISHED)).toList();
            Map<String, Double> statistic = getStatisticOrder(orderList, from, to);
            StatisticResponse response = StatisticResponse.wo(wo, countByStatus, statistic);
            return ApiResponse.success(response, SMessage.STATISTIC_FETCHED);
        }
    }
}
