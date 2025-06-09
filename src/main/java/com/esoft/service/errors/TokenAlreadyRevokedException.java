package com.esoft.service.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenAlreadyRevokedException extends BusinessException {

    public TokenAlreadyRevokedException() {
        super(HttpStatus.BAD_REQUEST, "TOKEN_REVOKED", "Token has already been revoked");
    }
}
