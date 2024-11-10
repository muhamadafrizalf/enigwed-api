package com.enigwed.constant;

public class SNotificationMessage {
    public static String NEW_ACCOUNT_REGISTERED(String name) {
        return String.format("A new account has been registered. %s has joined our application. Click here to activate their account.", name);
    }

    public static final String SUBSCRIPTION_EXPIRED = "Your account will be deactivated next month. Please renew your subscription to continue enjoying our services.";
    public static String RENEW_SUBSCRIPTION(int remainingMonths) {
        return String.format("Thank you for being with us! You have %d months remaining until your account expires. Consider renewing your subscription.", remainingMonths);
    }
    public static String LOYAL_CUSTOMER(int remainingMonths) {
        return String.format("Thank you for being a loyal customer! You have %d months remaining on your subscription.", remainingMonths);
    }

}
