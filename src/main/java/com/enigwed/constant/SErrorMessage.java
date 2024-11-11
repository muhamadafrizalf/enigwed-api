package com.enigwed.constant;

public class SErrorMessage {
    // Constraint violation
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";
    // Null or Empty
    public static final String ID_IS_REQUIRED = "ID is null or empty";
    public static final String NAME_IS_REQUIRED = "Name is null or empty";
    public static final String EMAIL_IS_REQUIRED = "Email null or empty";
    public static final String IMAGE_IS_NULL = "Image is null";
    public static final String BOOKING_CODE_IS_REQUIRED = "Booking code is null or empty";
    // Conflict
    public static final String NAME_UNIQUE = "Name already exists";
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String PHONE_ALREADY_EXIST = "Phone number already exists";
    public static final String NIB_ALREADY_EXIST = "NIB already exists";
    public static final String NPWP_ALREADY_EXIST = "NPWP already exists";
    // Invalid
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String CONFIRM_PASSWORD_MISMATCH = "Passwords do not match";
    public static final String ACCOUNT_NOT_ACTIVE = "Account is inactive";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String ACCESS_DENIED = "Access denied";
    // Not found
    public static final String BONUS_PACKAGE_NOT_FOUND = "Bonus package not found";
    public static final String WEDDING_PACKAGE_NOT_FOUND = "Wedding package not found";
    public static final String ORDER_NOT_FOUND = "Order not found";
    public static final String NO_PAYMENT_IMAGE_FOUND = "No payment image found";

    public static final String WEDDING_ORGANIZER_ID_IS_REQUIRED = "Wedding organizer id is null or empty";
    public static final String WEDDING_PACKAGE_ID_IS_REQUIRED = "Wedding package id is required";
    public static final String PAGE_OUT_OF_BOUND = "Requested page is out of bounds";
    public static final String SUBSCRIPTION_PACKAGE_NOT_FOUND = "Subscription price not found";
    public static final String SUBSCRIPTION_ID_IS_REQUIRED = "Subscription id is required";
    public static final String SUBSCRIPTION_NOT_FOUND = "Subscription not found";
    public static final String INVALID_DATE = "Start date must be before end date";
    public static final String INVALID_ORDER_STATUS = "Order not in the right status";
    public static final String USER_CREDENTIAL_ID_IS_REQUIRED = "User credential ID is required";
    public static final String JWT_INVALID = "JWT token is invalid or missing";
    public static final String JWT_AUTHENTICATION_FAILED = "JWT authentication failed";

    public static final String MANDATORY_BONUS_PACKAGE_NOT_FOUND = "Mandatory bonus package not found";
    public static final String BONUS_PACKAGE_QUANTITY_NOT_ADJUSTABLE = "Bonus package quantity not adjustable";
    public static final String INVALID_BONUS_PACKAGE_QUANTITY = "Invalid bonus package quantity";
    public static final String NOTIFICATION_NOT_FOUND = "Notification not found";

    public static final String PRODUCT_FORBIDDEN = "Can only add bonus package from own product";
    public static final String CANNOT_DELETE_BANK_ACCOUNT = "The bank account cannot be deleted because this wedding organizer has no other payment method available.";



    // Required
    public static final String BANK_ACCOUNT_ID_IS_REQUIRED = "Bank account ID is required";
    public static final String PRODUCT_ID_IS_REQUIRED = "Product ID is required";
    public static final String ORDER_ID_IS_REQUIRED = "Order ID is required";
    public static final String SUBSCRIPTION_PACKAGE_ID_IS_REQUIRED = "Subscription package id is required";

    // Not Found
    public static final String WEDDING_ORGANIZER_NOT_FOUND = "Wedding organizer not found";
    public static String WEDDING_ORGANIZER_NOT_FOUND(String id) {return String.format("Wedding organizer with ID %s not found", id);}
    public static String WEDDING_ORGANIZER_NOT_FOUND_EMAIL(String email) {return String.format("Wedding organizer with email %s not found", email);}
    public static String BANK_ACCOUNT_EMPTY(String weddingOrganizerName) {return String.format("No bank accounts found for wedding organizer %s", weddingOrganizerName);}
    public static String BANK_ACCOUNT_NOT_FOUND(String id) {return String.format("Bank account with ID %s not found.", id);}
    public static String PROVINCE_NOT_FOUND(String id) {return String.format("Province with ID %s not found", id);}
    public static String REGENCY_NOT_FOUND(String id) {return String.format("Regency with ID %s not found", id);}
    public static String DISTRICT_NOT_FOUND(String id) {return String.format("District with ID %s not found", id);}
    public static String PRODUCT_NOT_FOUND(String id) {return String.format("Product with ID %s not found", id);}
    public static String ORDER_NOT_FOUND(String id) {return String.format("Order with ID %s not found", id);}
    public static String WEDDING_PACKAGE_NOT_FOUND(String id) {return String.format("Wedding package with ID %s not found", id);}
    public static String SUBSCRIPTION_PACKAGE_NOT_FOUND(String id) {return String.format("Subscription package with ID %s not found", id);}
    public static String SUBSCRIPTION_NOT_FOUND(String id) {return String.format("Subscription with ID %s not found", id);}

    // Conflict
    public static String PROVINCE_ID_ALREADY_EXIST(String id, String name) {return String.format("Province with ID %s already exists with the name %s", id, name);}
    public static String REGENCY_ID_ALREADY_EXIST(String id, String name) {return String.format("Regency with ID %s already exists with the name %s", id, name);}
    public static String DISTRICT_ID_ALREADY_EXIST(String id, String name) {return String.format("District with ID %s already exists with the name %s", id, name);}
    public static String SUBSCRIPTION_PACKAGE_ALREADY_EXIST(String length) {return String.format("Subscription package with length %s already exists", length);}

    // Order
    public static String INVALID_ORDER_STATUS(EStatus before, String message) {return String.format("Cannot %s order, order not in %s status", message, before);}
    public static String INVALID_ORDER_NOT_STATUS(EStatus before, String message) {return String.format("Cannot %s order, order already %s", message, before);}


}
