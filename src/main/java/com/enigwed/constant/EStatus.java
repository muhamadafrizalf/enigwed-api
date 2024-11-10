package com.enigwed.constant;

import lombok.Getter;

@Getter
public enum EStatus {
    PENDING("Order is pending and awaiting processing"),
    REJECTED("Order has been rejected"),
    WAITING_FOR_PAYMENT("Order is waiting for payment"),
    CHECKING_PAYMENT("Order is currently checking payment status"),
    PAID("Payment has been made for the order"),
    CANCELED("Order has been canceled"),
    FINISHED("Order has been completed and finished");

    private final String description;

    EStatus(String description) {
        this.description = description;
    }

}

