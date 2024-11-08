package com.enigwed.constant;

public class Message {
    // Api Response Message
    public static final String CREATE_FAILED = "Create failed";
    public static final String FETCHING_FAILED = "Fetching failed";
    public static final String UPDATE_FAILED = "Update failed";
    public static final String DELETE_FAILED = "Delete failed";
    // Error Message
    public static final String ERROR = "An unexpected error occurred";
    // Auth
    public static final String REGISTER_SUCCESS = "Register success";
    public static final String REGISTER_FAILED = "Register failed";
    public static final String LOGIN_SUCCESS = "Login success";
    public static final String LOGIN_FAILED = "Login failed";
    public static final String REFRESH_TOKEN_SUCCESS = "Refresh token success";
    public static final String REFRESH_TOKEN_FAILED = "Refresh token failed";
    // City
    public static final String CITY_CREATED = "City created";
    public static final String CITY_FOUND = "City found";
    public static final String CITIES_FOUND = "Cities found";
    public static final String CITY_UPDATED = "City updated";
    public static final String CITY_DELETED = "City deleted";
    public static final String NO_CITY_FOUND = "No city found";
    // Image
    public static final String ERROR_CREATING_IMAGE_DIRECTORY = "Error while creating image directory";
    public static final String ERROR_CREATING_IMAGE = "Error while creating image";
    public static final String INVALID_IMAGE_TYPE = "Invalid image type";
    public static final String IMAGE_NOT_FOUND = "Image not found";
    public static final String IMAGE_UPDATED = "Image updated";
    public static final String IMAGE_DELETED = "Image deleted";
    public static final String IMAGE_FOUND = "Image found";
    // Wedding Organization
    public static final String WEDDING_ORGANIZER_FOUND = "Wedding organizer found";
    public static final String WEDDING_ORGANIZERS_FOUND = "Wedding organizers found";
    public static final String WEDDING_ORGANIZER_UPDATED = "Wedding organizer updated";
    public static final String WEDDING_ORGANIZER_DELETED = "Wedding organizer deleted";
    public static final String NO_WEDDING_ORGANIZER_FOUND = "No wedding organizer found";
    public static final String WEDDING_ORGANIZER_AVATAR_UPDATED = "Wedding organizer avatar updated";
    public static final String WEDDING_ORGANIZER_AVATAR_DELETED = "Wedding organizer avatar deleted";
    public static final String WEDDING_ORGANIZERS_ACTIVATED = "Wedding organizer activated";
    // Product
    public static final String PRODUCT_CREATED = "Product created";
    public static final String PRODUCT_FOUND = "Product found";
    public static final String NO_PRODUCT_FOUND = "No product found";
    public static final String PRODUCTS_FOUND = "Products found";
    public static final String PRODUCT_UPDATED = "Product updated";
    public static final String PRODUCT_DELETED = "Product deleted";
    // Wedding Package
    public static final String WEDDING_PACKAGE_CREATED = "Wedding package created";
    public static final String WEDDING_PACKAGE_FOUND = "Wedding package found";
    public static final String NO_WEDDING_PACKAGE_FOUND = "No wedding package found";
    public static final String WEDDING_PACKAGES_FOUND = "Wedding packages found";
    public static final String WEDDING_PACKAGE_UPDATED = "Wedding package updated";
    public static final String WEDDING_PACKAGE_DELETED = "Wedding package deleted";
    // Order
    public static final String ORDER_CREATED = "Order created";
    public static final String ORDER_FOUND = "Order found";
    public static final String ORDER_UPDATED = "Order updated";
    public static final String NO_ORDER_FOUND = "No order found";
    // Notification
    public static final String NO_NOTIFICATION_FOUND = "No notification found";
    public static final String NOTIFICATION_FOUNDS = "Notification founds";
    public static final String READ_FAILED = "Read failed";
    public static final String NOTIFICATION_READ = "Notification read";
    // Address
    public static final String DATA_NOT_FOUND = "Data not found";
    public static final String PAGE_INVALID = "Page must be a positive number";
    public static final String SIZE_INVALID = "Size must be a positive number";
    public static final String NO_SUBSCRIPTION_PRICE_FOUND = "No subscription price found";
    public static final String SUBSCRIPTION_PRICES_FOUND = "Subscription prices found";
    public static final String SUBSCRIPTION_PRICE_FOUND = "Subscription price found";
    public static final String SUBSCRIPTION_PRICE_CREATED = "Subscription price created";
    public static final String SUBSCRIPTION_PRICE_UPDATED = "Subscription price updated";
    public static final String SUBSCRIPTION_PRICE_DELETED = "Subscription price deleted";
    public static final String SUBSCRIPTION_PAID = "Subscription paid";
    public static final String SUBSCRIPTION_FOUND = "Subscription found";
    public static final String NO_SUBSCRIPTION_FOUND = "No subscription found";
    public static final String SUBSCRIPTIONS_FOUND = "Subscriptions found";
    public static final String SUBSCRIPTION_PAYMENT_CONFIRMED = "Subscription payment confirmed";
    public static final String STATISTIC_FETCHED = "Statistic fetched";

    public static String NEW_ACCOUNT_REGISTRATION(String weddingOrganizerName){
        return "New account registered: " + weddingOrganizerName + " join our application, click to activate their account";
    }
    public static String NEW_ORDER_RECEIVED(String name) {
        return "New order received: Order receive from customer " + name + ", click to check order detail.";
    }
    public static String CONFIRM_PAYMENT(String name) {
        return "Payment received: Payment receive from " + name + ", click to check order detail.";
    }
    public static String ORDER_CANCELED(String name) {
        return "Order canceled: Order from " + name + " has been canceled, click to check order detail.";
    }
    public static String ORDER_FINISHED(String name) {
        return "Order from " + name + " has been finished, click to check order detail.";
    }
    public static String ORDER_PAID(String name) {
        return "Order from " + name + " has been paid, click to check order detail.";
    }
}
