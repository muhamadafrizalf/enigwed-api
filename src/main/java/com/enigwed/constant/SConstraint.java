package com.enigwed.constant;

public class SConstraint {
    /* REQUIRED */
    //// ID BLANK
    public static final String WEDDING_ORGANIZER_ID_BLANK = "Wedding organizer ID is required";
    public static final String WEDDING_PACKAGE_ID_BLANK = "Wedding Package ID is required";
    public static final String PRODUCT_ID_BLANK = "Product ID is required";
    public static final String PROVINCE_ID_BLANK = "Province ID is required";
    public static final String REGENCY_ID_BLANK = "Regency ID is required";
    public static final String DISTRICT_ID_BLANK = "District ID is required";
    public static final String SUBSCRIPTION_PACKAGE_ID_BLANK = "Subscription package ID is required";
    public static final String ORDER_ID_BLANK = "Order ID is required";
    //// NAME BLANK
    public static final String WEDDING_ORGANIZER_NAME_BLANK = "Wedding organizer name is required";
    public static final String WEDDING_PACKAGE_NAME_BLANK = "Wedding package name is required";
    public static final String PRODUCT_NAME_BLANK = "Product name is required";
    public static final String PROVINCE_NAME_BLANK = "province name is required";
    public static final String REGENCY_NAME_BLANK = "Regency name is required";
    public static final String DISTRICT_NAME_BLANK = "District Name is required";
    public static final String BANK_NAME_BLANK = "Bank name is required";
    public static final String SUBSCRIPTION_PACKAGE_NAME_BLANK = "Subscription package name is required";
    public static final String ACCOUNT_NAME_BLANK = "Account name is required";
    public static final String CUSTOMER_NAME_BLANK = "Customer name is required";
    //// BLANK
    public static final String ADDRESS_BLANK = "Address is required";
    public static final String NPWP_BLANK = "NPWP is required";
    public static final String NIB_BLANK = "NIB is required";
    public static final String PHONE_BLANK = "Phone number is required";
    public static final String EMAIL_BLANK = "Email is required";
    public static final String PASSWORD_BLANK = "Password is required";
    public static final String CONFIRM_PASSWORD_BLANK = "Confirm Password is required";
    public static final String BANK_CODE_BLANK = "Bank code is required";
    public static final String ACCOUNT_NUMBER_BLANK = "Account number is required";
    public static final String WEDDING_ORGANIZER_DESCRIPTION_BLANK = "Wedding organizer description is required";
    public static final String WEDDING_PACKAGE_DESCRIPTION_BLANK = "Wedding package description is required";
    public static final String PRODUCT_DESCRIPTION_BLANK = "Product description is required";
    public static final String TOKEN_IS_BLANK = "Token is required";
    //// NULL OBJECT
    public static final String WEDDING_DATE_NULL = "Wedding date is required";
    public static final String CUSTOMER_NULL = "Customer cannot is required";
    public static final String RATING_NULL = "Rating is required";
    public static final String QUANTITY_NULL = "Quantity is required";
    public static final String PRICE_NULL = "Price is required";
    public static final String PROVINCE_NULL = "Province is required";
    public static final String REGENCY_NULL = "Regency is required";
    public static final String DISTRICT_NULL = "District is required";
    public static final String SUBSCRIPTION_PACKAGE_LENGTH_NULL = "Subscription package length is required";
    public static final String PAYMENT_IMAGE_NULL = "Payment image is required";
    /* INVALID */
    //// INVALID PATTERN
    public static final String EMAIL_INVALID = "Invalid email format";
    public static final String PHONE_INVALID = "Invalid phone number";
    public static final String ACCOUNT_NUMBER_INVALID = "Account number must contain only numbers";
    public static final String BANK_CODE_INVALID = "Bank code must contain only numbers";
    public static final String NIB_INVALID = "NIB must contain only numbers";
    //// INVALID NUMBER
    public static final String PAGE_INVALID = "Page must be a positive number";
    public static final String SIZE_INVALID = "Size must be a positive number";
    public static final String PRICE_INVALID = "Price must be a positive number";
    public static final String QUANTITY_INVALID = "Quantity must be a positive number";
    public static final String RATING_INVALID = "Rating can only contains integer number from 0 to 5";
    //// INVALID DATE
    public static final String WEDDING_DATE_INVALID = "Wedding date must be in the future";
    //// MIN MAX
    public static final String PASSWORD_MIN_6 = "Password must be at least 6 characters long";
    public static final String DESCRIPTION_MAX_1000 = "Description must be at most 1000 characters";
    public static final String DESCRIPTION_MAX_10000 = "Description must be at most 1000 characters";
    public static final String ADDRESS_MAX_1000 = "Address must be at most 1000 characters";
    public static final String COMMENT_MAX_500 = "Comment must be at most 500 characters";
}
