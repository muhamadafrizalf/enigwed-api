package com.enigwed.constant;

public class ErrorMessage {
    public static final String CONSTRAINT_VIOLATION = "Constraint violation";

    // Null or Empty
    public static final String ID_IS_REQUIRED = "ID is null or empty";
    public static final String NAME_IS_REQUIRED = "Name is null or empty";
    public static final String EMAIL_IS_REQUIRED = "Email null or empty";
    public static final String TOKEN_IS_BLANK = "Token is blank";
    public static final String IMAGE_IS_NULL = "Image is null";

    // Auth
    public static final String EMAIL_ALREADY_IN_USE = "Email already in use";
    public static final String INVALID_EMAIL_OR_PASSWORD = "Invalid email or password";
    public static final String CONFIRM_PASSWORD_MISMATCH = "Passwords do not match";
    public static final String ACCOUNT_NOT_ACTIVE = "Account not active";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String PHONE_ALREADY_EXIST = "Phone number already exists";
    public static final String NIB_ALREADY_EXIST = "NIB already exists";
    public static final String NPWP_ALREADY_EXIST = "NPWP already exists";


    // City
    public static final String NAME_UNIQUE = "Name already exists";
    public static final String CITY_NOT_FOUND = "City not found";
}
