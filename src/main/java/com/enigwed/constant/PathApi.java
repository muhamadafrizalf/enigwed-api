package com.enigwed.constant;

public class PathApi {
    // Base url
    public static final String VERSION = "/v1";
    public static final String BASE_URL = "/api" + VERSION;

    // Accessibility
    public static final String PROTECTED = "/protected";
    public static final String PUBLIC = "/public";

    // Auth API
    public static final String AUTH = BASE_URL + "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";

    // City
    public static final String PUBLIC_CITY = BASE_URL + PUBLIC + "/cities";
    public static final String PUBLIC_CITY_ID = PUBLIC_CITY + "/{id}";
    public static final String PROTECTED_CITY = BASE_URL + PROTECTED + "/cities";
    public static final String PROTECTED_CITY_ID = PROTECTED_CITY + "/{id}";


}
