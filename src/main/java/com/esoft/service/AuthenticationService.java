package com.esoft.service;

import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;

public interface AuthenticationService {

    TokenResponseDTO getToken(String username, String password);

    void revokeToken(String token);

    AuthorizationDTO validateToken(String token);

    TokenResponseDTO refreshToken(String refreshToken);
}
