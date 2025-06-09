package com.esoft.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class TokenNotFoundException extends BusinessException {
    public TokenNotFoundException() {
        super(HttpStatus.NOT_FOUND, "TOKEN_NOT_FOUND", "Token not found or already invalid");
    }
}
