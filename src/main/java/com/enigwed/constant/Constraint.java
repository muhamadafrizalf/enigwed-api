package com.enigwed.constant;

public class Constraint {
    // General
    public static final String NAME_BLANK = "Name is required";
    public static final String DESCRIPTION_MAX = "Description must be at most 1000 characters";
    public static final String DESCRIPTION_MAX_10000 = "Description must be at most 1000 characters";
    public static final String ADDRESS_MAX = "Address must be at most 1000 characters";
    // Register Request
    public static final String WEDDING_ORGANIZER_NAME_BLANK = "Wedding organizer name is required";
    public static final String ADDRESS_BLANK = "Address is required";
    public static final String NPWP_BLANK = "NPWP is required";
    public static final String NIB_BLANK = "NIB is required";
    public static final String PROVINCE_ID_BLANK = "Province ID is required";
    public static final String PROVINCE_NAME_BLANK = "province name is required";
    public static final String REGENCY_ID_BLANK = "Regency ID is required";
    public static final String REGENCY_NAME_BLANK = "Regency name is required";
    public static final String DISTRICT_ID_BLANK = "District ID is required";
    public static final String DISTRICT_NAME_BLANK = "District Name is required";
    public static final String PHONE_BLANK = "Phone number is required";
    public static final String EMAIL_VALID = "Email should be valid";
    public static final String EMAIL_BLANK = "Email is required";
    public static final String PASSWORD_MIN = "Password must be at least 6 characters long";
    public static final String PASSWORD_BLANK = "Password is required";
    public static final String CONFIRM_PASSWORD_BLANK = "Confirm Password is required";
    // Bonus Package Request
    public static final String PRICE_POSITIVE = "Price must be a positive number";
    public static final String QUANTITY_POSITIVE = "Quantity must be a positive number";
    // Bonus Detail Request
    public static final String PRODUCT_ID_BLANK = "Product ID is required";
    // Order Request
    public static final String WEDDING_PACKAGE_ID_BLANK = "Wedding Package ID is required";
    public static final String WEDDING_DATE_NULL = "Wedding date cannot be null";
    public static final String WEDDING_DATE_FUTURE = "Wedding date must be in the future";
    public static final String CUSTOMER_NULL = "Customer cannot be null";
    // Paging Request
    public static final String PAGE_POSITIVE = "Page must be a positive number";
    public static final String SIZE_POSITIVE = "Size must be a positive number";

    // Refresh Token
    public static final String TOKEN_IS_BLANK = "Token is required";
    public static final String PRODUCT_NAME_BLANK = "Product name is required";
    public static final String PRODUCT_DESCRIPTION_BLANK = "Product description is required";
    public static final String QUANTITY_NULL = "Quantity is required";
    public static final String COMMENT_MAX_500 = "Comment must be at most 500 characters";
    public static final String RATING_NULL = "Rating is required";
    public static final String INVALID_RATING = "Rating can only contains integer number from 0 to 5";
    public static final String SUBSCRIPTION_LENGTH_NULL = "Subscription length is required";
    public static final String PAYMENT_IMAGE_NULL = "Payment image is required";
    public static final String SUBSCRIPTION_PRICE_ID_BLANK = "Subscription price id is required";
}
