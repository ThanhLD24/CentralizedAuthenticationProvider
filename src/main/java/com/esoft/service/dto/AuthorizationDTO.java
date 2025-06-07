package com.esoft.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class AuthorizationDTO {
    private boolean authorized;
    private String message;
    private AdminUserDTO user;
    public AuthorizationDTO(boolean authorized)
    {
        this.authorized = authorized;
    }

    public AuthorizationDTO(boolean authorized, String message) {
        this.authorized = authorized;
        this.message = message;
    }
}
