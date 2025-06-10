package com.esoft.service;

import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface AuthenticationService {

    TokenResponseDTO createToken(String username, String password);

    void revokeToken(String token);

    AuthorizationDTO validateToken(String token);

    TokenResponseDTO refreshToken(String refreshToken);

    TokenResponseDTO createTokenFromOAuth2(OAuth2AuthenticationToken authenticationToken);
}
