package com.enigwed.util;

import com.enigwed.constant.EStatus;
import com.enigwed.constant.ESubscriptionPaymentStatus;
import com.enigwed.constant.EUserStatus;
import com.enigwed.entity.Order;
import com.enigwed.entity.Subscription;
import com.enigwed.entity.WeddingOrganizer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatisticUtil {

    private EUserStatus getUserStatus(WeddingOrganizer wo) {
        if (wo.getUserCredential().isActive()) {
            return EUserStatus.ACTIVE;
        } else if (wo.getDeletedAt() == null) {
            return EUserStatus.INACTIVE;
        } else {
            return EUserStatus.DELETED;
        }
    }

    public Map<String, Integer> countWeddingOrganizerByStatus(List<WeddingOrganizer> woList) {
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

    public Map<String, Integer> countOrderByStatus(List<Order> orderList) {
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

    public Map<String, Integer> countBySubscriptionPaymentStatus(List<Subscription> list) {
        Map<String, Integer> map = new HashMap<>();
        map.put("ALL", 0);
        for (ESubscriptionPaymentStatus status : ESubscriptionPaymentStatus.values()) {
            map.put(status.name(), 0);
        }
        for (Subscription subscription : list) {
            map.put("ALL", map.get("ALL") + 1);
            map.put(subscription.getStatus().name(), map.get(subscription.getStatus().name()) + 1);
        }
        return map;
    }

}
