package com.esoft.service;

import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.AuthorizationDataDTO;
import com.esoft.service.dto.Result;
import com.esoft.service.dto.TokenResponseDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface AuthenticationService {

    AuthorizationDataDTO createToken(String username, String password);

    void revokeToken(String token);

    Result<AuthorizationDataDTO> validateToken(String token);

    AuthorizationDataDTO refreshToken(String refreshToken);

    AuthorizationDataDTO createToken(Authentication authentication);
}
