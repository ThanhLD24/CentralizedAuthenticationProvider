package com.esoft.service.errors;

import org.springframework.http.HttpStatus;

public class ApplicationSystemNotFound extends BusinessException {

    public ApplicationSystemNotFound(String message) {
        super(HttpStatus.BAD_REQUEST, "APPLICATION_NOT_FOUND", "ApplicationSystem not found: " + message);
    }

    public ApplicationSystemNotFound() {
        super(HttpStatus.BAD_REQUEST, "APPLICATION_NOT_FOUND", "ApplicationSystem not found");
    }
}
