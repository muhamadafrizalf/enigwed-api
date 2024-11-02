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
    // Wedding Organizer
    public static final String PUBLIC_WO = PUBLIC + "/wedding-organizers";
    public static final String PUBLIC_WO_ID = PUBLIC_WO + "/{id}";
    public static final String PROTECTED_WO = PROTECTED + "/wedding-organizers";
    public static final String PROTECTED_WO_ID = PROTECTED_WO + "/{id}";
    public static final String PROTECTED_WO_ID_IMAGES = PROTECTED_WO_ID + "/images";
    public static final String PROTECTED_WO_ID_ACTIVATE = PROTECTED_WO_ID + "/activate";
    // Bonus Package
    public static final String PUBLIC_BONUS_PACKAGE = PUBLIC + "/bonus-packages";
    public static final String PUBLIC_BONUS_PACKAGE_ID = PUBLIC_BONUS_PACKAGE + "/{id}";
    public static final String PROTECTED_BONUS_PACKAGE = PROTECTED + "/bonus-packaged";
    public static final String PROTECTED_BONUS_PACKAGE_ID = PROTECTED_BONUS_PACKAGE + "/{id}";
    public static final String PROTECTED_BONUS_PACKAGE_ID_IMAGES = PROTECTED_BONUS_PACKAGE_ID + "/images";
    public static final String PROTECTED_BONUS_PACKAGE_ID_IMAGES_ID = PROTECTED_BONUS_PACKAGE_ID_IMAGES + "/{image-id}";
}
