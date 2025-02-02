package com.enigwed.constant;

public class SMessage {
    // Api Response Message
    public static final String CREATE_FAILED = "Create failed";
    public static final String FETCHING_FAILED = "Fetching failed";
    public static final String UPDATE_FAILED = "Update failed";
    public static final String DELETE_FAILED = "Delete failed";
    // Error Message
    public static final String ERROR = "An unexpected error occurred";
    public static final String UNAUTHORIZED = "Invalid or missing authentication token";
    // Auth
    public static final String REGISTER_SUCCESS = "Register success";
    public static final String REGISTER_FAILED = "Register failed";
    public static final String LOGIN_SUCCESS = "Login success";
    public static final String LOGIN_FAILED = "Login failed";
    public static final String REFRESH_TOKEN_SUCCESS = "Refresh token success";
    public static final String REFRESH_TOKEN_FAILED = "Refresh token failed";
    public static final String AUTHENTICATION_FAILED = "Authentication failed";
    // Wedding Organizer
    public static final String NO_WEDDING_ORGANIZER_FOUND = "No wedding organizer found";
    public static final String WEDDING_ORGANIZER_FOUND = "Wedding organizer found";
    public static String WEDDING_ORGANIZERS_FOUND(int size) {return String.format("%d wedding organizer(s) found", size);}
    public static String WEDDING_ORGANIZER_FOUND(String id) {return String.format("Wedding organizer with ID %s found", id);}
    public static String WEDDING_ORGANIZER_UPDATED(String id) {return String.format("Wedding organizer with ID %s successfully updated", id);}
    public static String WEDDING_ORGANIZER_DELETED(String id) {return String.format("Wedding organizer with ID %s successfully deleted", id);}
    public static String WEDDING_ORGANIZER_AVATAR_UPDATED(String weddingOrganizerName) {return String.format("The avatar for wedding organizer %s has been updated", weddingOrganizerName);}
    public static String WEDDING_ORGANIZER_AVATAR_DELETED(String weddingOrganizerName) {return String.format("The avatar for wedding organizer %s has been deleted", weddingOrganizerName);}
    public static String WEDDING_ORGANIZERS_ACTIVATED(String name) {return String.format("Wedding organizers for %s have been activated", name);}
    public static String WEDDING_ORGANIZERS_DEACTIVATED(String name) {return String.format("Wedding organizers for %s have been deactivated", name);}
    // Bank Account
    public static String NO_BANK_ACCOUNT_FOUND(String weddingOrganizerName) {return String.format("No bank account found for wedding organizer %s", weddingOrganizerName);}
    public static String BANK_ACCOUNTS_FOUND(String weddingOrganizerName, int size) {return String.format("Found %d bank account(s) for wedding organizer %s", size, weddingOrganizerName);}
    public static String BANK_ACCOUNT_FOUND(String id) {
        return String.format("Bank account with ID %s found", id);
    }
    public static String BANK_ACCOUNT_CREATED(String id) {return String.format("Bank account with ID %s successfully created", id);}
    public static String BANK_ACCOUNT_UPDATED(String id) {return String.format("Bank account with ID %s successfully updated", id);}
    public static String BANK_ACCOUNT_DELETED(String id) {return String.format("Bank account with ID %s successfully deleted", id);}
    // Product
    public static final String NO_PRODUCT_FOUND = "No product found";
    public static String PRODUCTS_FOUND(int size) {return String.format("Found %d product(s)", size);}
    public static String NO_PRODUCT_FOUND(String weddingOrganizerName) {return String.format("No product found for wedding organizer %s", weddingOrganizerName);}
    public static String PRODUCTS_FOUND(String weddingOrganizerName, int size) {return String.format("Found %d product(s) for wedding organizer %s", size, weddingOrganizerName);}
    public static String PRODUCT_FOUND(String id) {return String.format("Product with ID %s found", id);}
    public static String PRODUCT_CREATED(String id) {return String.format("Product with ID %s successfully created", id);}
    public static String PRODUCT_UPDATED(String id) {return String.format("Product with ID %s successfully updated", id);}
    public static String PRODUCT_DELETED(String id) {return String.format("Product with ID %s successfully deleted", id);}
    public static String PRODUCT_IMAGE_ADDED(String productName) {return String.format("A new image has been added for the product %s", productName);}
    public static String PRODUCT_IMAGE_DELETED(String productName, String imageId) {return String.format("The image with ID %s has been deleted from the product %s", imageId, productName);}
    // Wedding Package
    public static final String NO_WEDDING_PACKAGE_FOUND = "No wedding package found";
    public static String WEDDING_PACKAGES_FOUND(int size) {return String.format("%d wedding package(s) found", size);}
    public static String WEDDING_PACKAGE_FOUND(String id) {return String.format("Wedding package with ID %s found", id);}
    public static String WEDDING_PACKAGE_CREATED(String id) {return String.format("Wedding package with ID %s successfully created", id);}
    public static String WEDDING_PACKAGE_UPDATED(String id) {return String.format("Wedding package with ID %s successfully updated", id);}
    public static String WEDDING_PACKAGE_DELETED(String id) {return String.format("Wedding package with ID %s successfully deleted", id);}
    public static String WEDDING_PACKAGE_IMAGE_ADDED(String weddingPackageName) {return String.format("A new image has been added for the wedding package %s", weddingPackageName);}
    public static String WEDDING_PACKAGE_IMAGE_DELETED(String weddingPackageName, String imageId) {return String.format("The image with ID %s has been deleted from the wedding package %s", imageId, weddingPackageName);}
    // Order
    public static final String NO_ORDER_FOUND = "No order found";
    public static String ORDERS_FOUND(int size) {return String.format("%d order(s) found", size);}
    public static String ORDER_FOUND(String id) {return String.format("Order with ID %s found", id);}
    public static String ORDER_FOUND_BOOK_CODE(String bookCode) {return String.format("Order with book code '%s' found", bookCode);}
    public static String ORDER_CREATED(String bookCode) {return String.format("Order with book code '%s' has been created", bookCode);}
    public static String ORDER_ACCEPTED(String bookCode) {return String.format("Order with book code '%s' accepted", bookCode);}
    public static String ORDER_REJECTED(String bookCode) {return String.format("Order with book code '%s' rejected", bookCode);}
    public static String ORDER_PAID(String bookCode) {return String.format("Order with book code '%s' has been paid", bookCode);}
    public static String ORDER_CANCELED(String bookCode) {return String.format("Order with book code '%s' has been canceled", bookCode);}
    public static String PAYMENT_CONFIRMED(String bookCode) {return String.format("Payment for order with book code '%s' has been confirmed", bookCode);}
    public static String ORDER_FINISHED(String bookCode) {return String.format("Order with book code '%s' has been finished", bookCode);}
    public static String ORDER_REVIEWED(String bookCode) {return String.format("Order with book code '%s' has been reviewed", bookCode);}
    // Subscription Package
    public static final String NO_SUBSCRIPTION_PACKAGE_FOUND = "No subscription package found";
    public static String SUBSCRIPTION_PACKAGES_FOUND(int size) {return String.format("Found %d subscription package(s)", size);}
    public static String SUBSCRIPTION_PACKAGE_FOUND(String id) {return String.format("Subscription package with ID %s found", id);}
    public static String SUBSCRIPTION_PACKAGE_CREATED(String id) {return String.format("Subscription package with ID %s successfully created", id);}
    public static String SUBSCRIPTION_PACKAGE_UPDATED(String id) {return String.format("Subscription package with ID %s successfully updated", id);}
    public static String SUBSCRIPTION_PACKAGE_DELETED(String id) {return String.format("Subscription package with ID %s successfully deleted", id);}
    // Subscription
    public static final String NO_SUBSCRIPTION_FOUND = "No subscription found";
    public static final String NO_ACTIVE_SUBSCRIPTION_FOUND = "No active subscription found";
    public static String SUBSCRIPTIONS_FOUND(int size) {return String.format("Found %d subscription(s)", size);}
    public static String NO_ACTIVE_SUBSCRIPTION_FOUND(String weddingOrganizerName) {return String.format("No active subscription found for wedding organizer %s", weddingOrganizerName);}
    public static String SUBSCRIPTION_FOUND(String subscriptionId) {return String.format("Subscription with ID %s found", subscriptionId);}
    public static String SUBSCRIPTION_PAID(String subscriptionId) {return String.format("A new subscription with ID %s has been paid", subscriptionId);}
    public static String SUBSCRIPTION_CONFIRMED(String subscriptionId) {return String.format("Subscription with ID %s has been confirmed", subscriptionId);}
    public static String ACTIVE_SUBSCRIPTIONS_FOUND(int size) {return String.format("Found %d active subscription(s)", size);}
    public static String ACTIVE_SUBSCRIPTIONS_FOUND(int size, String weddingOrganizerName) {return String.format("Found %d active subscription(s) for wedding organizer %s", size, weddingOrganizerName);}
    // Notification
    public static final String NO_NOTIFICATION_FOUND = "No notification found";
    public static String NOTIFICATION_FOUNDS(int size) {return String.format("Found %d notification(s)", size);}
    public static String NOTIFICATION_READ(String id) {return String.format("Notification with ID %s has been read", id);}
    public static String READ_FAILED(String id) {return String.format("Cannot read notification with ID %s", id);}
    // Statistic
    public static final String STATISTIC_FETCHED = "Statistic fetched";

    // Image
    public static final String ERROR_CREATING_IMAGE_DIRECTORY = "Error while creating image directory";
    public static final String ERROR_CREATING_IMAGE = "Error while creating image";
    public static final String INVALID_IMAGE_TYPE = "Invalid image type";
    public static final String IMAGE_NOT_FOUND = "Image not found";
    public static final String IMAGE_UPDATED = "Image updated";
    public static final String IMAGE_FOUND = "Image found";

    public static String IMAGE_NOT_FOUND(String id) {return String.format("Image with ID %s not found", id);}
}
