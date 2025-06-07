package com.esoft.service.impl;

import com.esoft.service.AuthenticationService;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.utils.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JWTUtil jwtUtil;
    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, JWTUtil jwtUtil) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
         this.jwtUtil = jwtUtil;

    }

    @Override
    public TokenResponseDTO getToken(String username, String password) {
        boolean rememberMe = false;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            username,
            password
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.createToken(authentication, rememberMe);
        String refreshToken  = "";
        return new TokenResponseDTO(jwt, refreshToken);
    }

    @Override
    public boolean revokeToken(String token) {
        // Implement logic to revoke the provided token
        return true;
    }

    @Override
    public AuthorizationDTO validateToken(String token) {
        // Implement logic to validate the provided token
        return new AuthorizationDTO();
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) {
        // Implement logic to refresh the provided refresh token
        return new TokenResponseDTO();
    }
}
