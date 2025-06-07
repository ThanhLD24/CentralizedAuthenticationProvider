package com.esoft.service.impl;

import com.esoft.domain.AccessToken;
import com.esoft.domain.RefreshToken;
import com.esoft.domain.User;
import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.repository.AccessTokenRepository;
import com.esoft.repository.RefreshTokenRepository;
import com.esoft.repository.UserRepository;
import com.esoft.service.AuthenticationService;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.utils.JWTUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final AccessTokenRepository accessTokenRepository;
    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, JWTUtil jwtUtil,
                                     RefreshTokenRepository refreshTokenRepository, UserRepository userRepository, AccessTokenRepository accessTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.accessTokenRepository = accessTokenRepository;
    }

    @Override
    @Transactional
    public TokenResponseDTO getToken(String username, String password) {
        boolean rememberMe = false;
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            username,
            password
        );
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
        Optional<User> user = userRepository.findOneByLogin(authentication.getName());

        String jwt = jwtUtil.createToken(authentication, rememberMe);


        String refreshTokenValue = jwtUtil.createRefreshToken();
        String hashedRefreshTokenValue = jwtUtil.hashToken(refreshTokenValue);

        AccessToken accessToken = new AccessToken();
        accessToken.setHashedToken(jwtUtil.hashToken(jwt));
        accessToken.setStatus(TokenStatus.ACTIVE);
        accessToken.setCreatedDate(Instant.now());
        accessTokenRepository.save(accessToken);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setHashedToken(hashedRefreshTokenValue);
        refreshToken.setStatus(TokenStatus.ACTIVE);
        refreshToken.setCreatedDate(Instant.now());
        refreshToken.setExpiryDate(Instant.now().plusSeconds(jwtUtil.getRefreshTokenValidityInSeconds()));
        refreshToken.setUser(user.get());
        refreshTokenRepository.save(refreshToken);

        return new TokenResponseDTO(jwt, refreshTokenValue);
    }

    @Override
    public boolean revokeToken(String token) {
        // find the access token by the provided token
        Optional<AccessToken> accessTokenOptional = accessTokenRepository.findOneByHashedToken(jwtUtil.hashToken(token));
        if (accessTokenOptional.isEmpty()) {
            return false; // Token not found
        } else {
            AccessToken accessToken = accessTokenOptional.get();
            accessToken.setStatus(TokenStatus.REVOKED);
            accessTokenRepository.save(accessToken);
        }
        return true;
    }

    @Override
    public AuthorizationDTO validateToken(String token) {
        // find the access token by the provided token
        Optional<AccessToken> accessTokenOptional = accessTokenRepository.findOneByHashedToken(jwtUtil.hashToken(token));
        if (accessTokenOptional.isPresent()) {
            AccessToken accessToken = accessTokenOptional.get();
            if (accessToken.getStatus() == TokenStatus.ACTIVE) {
                // Token is valid
                return new AuthorizationDTO(true, accessToken.getUser().getLogin());
            } else {
                // Token is revoked or invalid
                return new AuthorizationDTO(false, " Token is revoked or invalid", null);
            }
        }
        return new AuthorizationDTO(false);
    }

    @Override
    public TokenResponseDTO refreshToken(String refreshToken) {
        // Implement logic to refresh the provided refresh token
        return new TokenResponseDTO();
    }
}
