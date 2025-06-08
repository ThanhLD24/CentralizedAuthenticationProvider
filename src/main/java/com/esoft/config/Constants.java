package com.esoft.config;

/**
 * Application constants.
 */
public final class Constants {

    // Regex for acceptable logins
    public static final String LOGIN_REGEX = "^(?>[a-zA-Z0-9!$&*+=?^_`{|}~.-]+@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*)|(?>[_.@A-Za-z0-9-]+)$";

    public static final String SYSTEM = "system";
    public static final String DEFAULT_LANGUAGE = "en";

    private Constants() {}

    public interface AUTH_MESSAGE {
        String INVALID_CREDENTIALS = "Invalid credentials";
        String TOKEN_VALID = "Token valid";
        String TOKEN_EXPIRED = "Token expired";
        String TOKEN_INVALID = "Token invalid";
        String TOKEN_REVOKED = "Token revoked";
        String TOKEN_NOT_FOUND = "Token not found";
        String ACCESS_DENIED = "Access denied";
    }
}
