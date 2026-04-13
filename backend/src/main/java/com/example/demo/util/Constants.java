package com.example.demo.util;

/**
 * Constants - Application-wide constants
 */
public class Constants {

    public static final String API_PREFIX = "/api";
    public static final String USERS_ENDPOINT = "/users";
    public static final String HEALTH_ENDPOINT = "/health";

    // HTTP Status Messages
    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    public static final String USER_NOT_FOUND = "User not found";

    private Constants() {
        // Private constructor to prevent instantiation
    }

}
