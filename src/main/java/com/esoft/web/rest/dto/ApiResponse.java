package com.esoft.web.rest.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private ResponseStatus status;
    private String message;
    private T data;
    private Object errors;

    public ApiResponse(ResponseStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}

