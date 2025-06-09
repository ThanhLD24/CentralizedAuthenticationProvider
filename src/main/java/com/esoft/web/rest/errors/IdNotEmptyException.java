package com.esoft.web.rest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdNotEmptyException extends BusinessException {

    public IdNotEmptyException() {
        super(HttpStatus.BAD_REQUEST, "ID_NOT_EMPTY", "A new user cannot already have an ID");
    }
}
