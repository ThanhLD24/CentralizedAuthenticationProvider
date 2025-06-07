package com.esoft.web.rest.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

}
