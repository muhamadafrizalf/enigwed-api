package com.enigwed.constant;

public class PathApi {
    // Base url
//    public static final String VERSION = "/v1";
//    public static final String BASE_URL = "/api" + VERSION;
    public static final String BASE_URL = "/api";

    // Accessibility
    public static final String PROTECTED = BASE_URL + "/protected";
    public static final String PUBLIC = BASE_URL + "/public";

    // Auth API
    public static final String AUTH = BASE_URL + "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String REFRESH_TOKEN = "/refresh-token";

    // City
    public static final String PUBLIC_CITY = PUBLIC + "/cities";
    public static final String PUBLIC_CITY_ID = PUBLIC_CITY + "/{id}";
    public static final String PROTECTED_CITY = PROTECTED + "/cities";
    public static final String PROTECTED_CITY_ID = PROTECTED_CITY + "/{id}";

    // Image
    public static final String PUBLIC_IMAGE = PUBLIC + "/images";
    public static final String PUBLIC_IMAGE_RESOURCE_ID = PUBLIC_IMAGE + "/resource/{id}";
    public static final String PUBLIC_IMAGE_ID = PUBLIC_IMAGE + "/{id}";
    public static final String PROTECTED_IMAGE = PROTECTED + "/images";
    public static final String PROTECTED_IMAGE_ID = PROTECTED_IMAGE + "/{id}";
    // Wedding organizer
    public static final String PUBLIC_WO = PUBLIC + "/wedding-organizer";
    public static final String PUBLIC_WO_ID = PUBLIC_WO + "/{id}";
    public static final String PROTECTED_WO = PROTECTED + "/wo";
    public static final String PROTECTED_WO_ID = PROTECTED_WO + "/{id}";
}
