package com.enigwed.constant;

public class Constraint {
    // General
    public static final String NAME_BLANK = "Name cannot be blank";
    public static final String DESCRIPTION_MAX = "Description must be at most 1000 characters";
    public static final String DESCRIPTION_MAX_10000 = "Description must be at most 1000 characters";
    // Register Request
    public static final String ADDRESS_BLANK = "Address cannot be blank";
    public static final String NPWP_BLANK = "NPWP cannot be blank";
    public static final String NIB_BLANK = "NIB cannot be blank";
    public static final String CITY_ID_BLANK = "City ID cannot be blank";
    public static final String PHONE_BLANK = "Phone number cannot be blank";
    public static final String EMAIL_VALID = "Email should be valid";
    public static final String EMAIL_BLANK = "Email cannot be blank";
    public static final String PASSWORD_MIN = "Password must be at least 6 characters long";
    public static final String PASSWORD_BLANK = "Password cannot be blank";
    public static final String CONFIRM_PASSWORD_BLANK = "Confirm Password cannot be blank";
    // Bonus Package Request
    public static final String PRICE_POSITIVE = "Price must be a positive number";
    public static final String QUANTITY_POSITIVE = "Quantity must be a positive number";
    // Bonus Detail Request
    public static final String BONUS_PACKAGE_ID_BLANK = "Bonus Package ID cannot be blank";
    // Order Request
    public static final String WEDDING_PACKAGE_ID_BLANK = "Wedding Package ID cannot be blank";
    public static final String WEDDING_DATE_NULL = "Wedding date cannot be null";
    public static final String WEDDING_DATE_FUTURE = "Wedding date must be in the future";
}
