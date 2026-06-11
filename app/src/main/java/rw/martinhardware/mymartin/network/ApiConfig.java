package rw.martinhardware.mymartin.network;

import rw.martinhardware.mymartin.BuildConfig;

public class ApiConfig {
    // Environment URLs
    private static final String DEV_BASE_URL = "http://192.168.100.156:8000/api";
    private static final String PROD_BASE_URL = "https://martin-logistics.nova.bi/api";

    // Automatically select base URL based on build type
    public static final String BASE_URL = BuildConfig.DEBUG ? DEV_BASE_URL : PROD_BASE_URL;

    // Auth endpoints
    public static final String AUTH_REQUEST_WHATSAPP_OTP = BASE_URL + "/mobile/auth/request-whatsapp-otp";
    public static final String AUTH_VERIFY_WHATSAPP_OTP = BASE_URL + "/mobile/auth/verify-whatsapp-otp";
    public static final String AUTH_VERIFY_FIREBASE_PHONE = BASE_URL + "/mobile/auth/verify-firebase-phone";
    public static final String AUTH_LOGOUT = BASE_URL + "/mobile/auth/logout";

    // Support endpoints
    public static final String SUPPORT_TICKETS = BASE_URL + "/mobile/support/tickets";
    public static final String SUPPORT_CATEGORIES = BASE_URL + "/mobile/support/categories";

    public static String supportTicketDetail(int ticketId) {
        return BASE_URL + "/mobile/support/tickets/" + ticketId;
    }

    public static String supportTicketMessages(int ticketId) {
        return BASE_URL + "/mobile/support/tickets/" + ticketId + "/messages";
    }

    // Helper method to get current environment
    public static String getCurrentEnvironment() {
        return BuildConfig.DEBUG ? "Development" : "Production";
    }
}
