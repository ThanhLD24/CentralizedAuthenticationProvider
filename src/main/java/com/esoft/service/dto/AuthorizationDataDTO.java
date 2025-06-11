package com.esoft.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthorizationDataDTO {
    private boolean authorized;
    private AdminUserDTO user;
    private TokenResponseDTO token;
    public AuthorizationDataDTO() {
        this.authorized = true;
    }

    public AuthorizationDataDTO(AdminUserDTO user) {
        this.authorized = true;
        this.user = user;
    }
}
