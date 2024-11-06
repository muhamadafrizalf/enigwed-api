package com.enigwed.constant;

public class PathApi {
    // Base url
//    public static final String VERSION = "/v1";
//    public static final String BASE_URL = "/api" + VERSION;
    public static final String BASE_URL = "/api";



    // Accessibility
    public static final String PUBLIC = BASE_URL + "/public";
    public static final String PROTECTED = BASE_URL + "/protected";
    // Auth API
    public static final String AUTH = BASE_URL + "/auth";
    public static final String LOGIN = "/login";
    public static final String REGISTER = "/register";
    public static final String REFRESH_TOKEN = "/refresh-token";
    // Wedding Organizer
    public static final String PUBLIC_WO = PUBLIC + "/wedding-organizers";
    public static final String PROTECTED_WO = PROTECTED + "/wedding-organizers";
    public static final String PUBLIC_WO_ID = PUBLIC_WO + "/{id}";
    public static final String PROTECTED_WO_ID = PROTECTED_WO + "/{id}";
    public static final String PROTECTED_WO_ID_IMAGE = PROTECTED_WO_ID + "/images";
    public static final String PROTECTED_WO_ID_ACTIVATE = PROTECTED_WO_ID + "/activate";
    // Bonus Package
    public static final String PUBLIC_PRODUCT = PUBLIC + "/products";
    public static final String PUBLIC_PRODUCT_ID = PUBLIC_PRODUCT + "/{id}";
    public static final String PROTECTED_PRODUCT = PROTECTED + "/products";
    public static final String PROTECTED_PRODUCT_ID = PROTECTED_PRODUCT + "/{id}";
    public static final String PROTECTED_PRODUCT_ID_IMAGE = PROTECTED_PRODUCT_ID + "/images";
    public static final String PROTECTED_PRODUCT_ID_IMAGE_ID = PROTECTED_PRODUCT_ID_IMAGE + "/{image-id}";
    // Wedding Package
    public static final String PUBLIC_WEDDING_PACKAGE = PUBLIC + "/wedding-packages";
    public static final String PUBLIC_WEDDING_PACKAGE_ID = PUBLIC_WEDDING_PACKAGE + "/{id}";
    public static final String PROTECTED_WEDDING_PACKAGE = PROTECTED + "/wedding-packages";
    public static final String PROTECTED_WEDDING_PACKAGE_ID = PROTECTED_WEDDING_PACKAGE + "/{id}";
    public static final String PROTECTED_WEDDING_PACKAGE_ID_IMAGE = PROTECTED_WEDDING_PACKAGE_ID + "/images";
    public static final String PROTECTED_WEDDING_PACKAGE_ID_IMAGE_ID = PROTECTED_WEDDING_PACKAGE_ID_IMAGE + "/{image-id}";
    // Order
    public static final String PUBLIC_ORDER = PUBLIC + "/orders";
    public static final String PUBLIC_ORDER_ID = PUBLIC_ORDER + "/{id}";
    public static final String PUBLIC_ORDER_ID_PAY = PUBLIC_ORDER_ID + "/pay";
    public static final String PUBLIC_ORDER_ID_CANCEL = PUBLIC_ORDER_ID + "/cancel";
    public static final String PUBLIC_ORDER_ID_REVIEW = PUBLIC_ORDER_ID + "/review";
    public static final String PROTECTED_ORDER = PROTECTED + "/orders";
    public static final String PROTECTED_ORDER_ID = PROTECTED_ORDER + "/{id}";
    public static final String PROTECTED_ORDER_ID_CONFIRM = PROTECTED_ORDER_ID + "/confirm";
    public static final String PROTECTED_ORDER_ID_ACCEPT = PROTECTED_ORDER_ID + "/accept";
    public static final String PROTECTED_ORDER_ID_REJECT = PROTECTED_ORDER_ID + "/reject";
    public static final String PROTECTED_ORDER_ID_FINISH = PROTECTED_ORDER_ID + "/finish";

    // Notification
    public static final String PROTECTED_NOTIFICATION = PROTECTED + "/notifications";
    public static final String PROTECTED_NOTIFICATION_ID = PROTECTED_NOTIFICATION + "/{id}";


    // Image
    public static final String PUBLIC_IMAGE = PUBLIC + "/images";
    public static final String PUBLIC_IMAGE_RESOURCE_ID = PUBLIC_IMAGE + "/resource/{id}";
    public static final String PUBLIC_IMAGE_ID = PUBLIC_IMAGE + "/{id}";
    public static final String PROTECTED_IMAGE = PROTECTED + "/images";
    public static final String PROTECTED_IMAGE_ID = PROTECTED_IMAGE + "/{id}";
    public static final String PUBLIC_NOTIFICATION = PUBLIC + "/notifications";
}
