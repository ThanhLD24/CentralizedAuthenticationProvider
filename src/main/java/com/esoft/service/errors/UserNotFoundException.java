package com.esoft.service.errors;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(String message) {
        super(HttpStatus.BAD_REQUEST, "USER_NOT_FOUND", "User not found: " + message);
    }
}
