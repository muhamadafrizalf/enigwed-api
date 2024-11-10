package com.enigwed.constant;

import lombok.Getter;

@Getter
public enum ESubscriptionLength {
    A_MONTH(1),
    TWO_MONTHS(2),
    THREE_MONTHS(3),
    FOUR_MONTHS(4),
    FIVE_MONTHS(5),
    SIX_MONTHS(6),
    SEVEN_MONTHS(7),
    EIGHT_MONTHS(8),
    NINE_MONTHS(9),
    TEN_MONTHS(10),
    ELEVEN_MONTHS(11),
    A_YEAR(12);

    private final int months;

    ESubscriptionLength(int months) {
        this.months = months;
    }

    public String getDescription() {
        return switch (this) {
            case A_MONTH -> "1 Month Subscription";
            case TWO_MONTHS -> "2 Month Subscription";
            case THREE_MONTHS -> "3 Month Subscription";
            case FOUR_MONTHS -> "4 Month Subscription";
            case FIVE_MONTHS -> "5 Month Subscription";
            case SIX_MONTHS -> "6 Month Subscription";
            case SEVEN_MONTHS -> "7 Month Subscription";
            case EIGHT_MONTHS -> "8 Month Subscription";
            case NINE_MONTHS -> "9 Month Subscription";
            case TEN_MONTHS -> "10 Month Subscription";
            case ELEVEN_MONTHS -> "11 Month Subscription";
            case A_YEAR -> "12 Month Subscription";
            default -> "Unknown Subscription";
        };
    }
}

