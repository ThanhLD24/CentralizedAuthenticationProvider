package com.esoft.service.impl;

import com.esoft.config.Constants;
import com.esoft.domain.TokenHistory;
import com.esoft.domain.User;
import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.domain.enumeration.TokenType;
import com.esoft.repository.UserRepository;
import com.esoft.service.AuthenticationService;
import com.esoft.service.TokenHistoryService;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.*;
import com.esoft.service.mapper.UserMapper;
import com.esoft.utils.JWTUtil;
import com.esoft.service.errors.TokenAlreadyRevokedException;
import com.esoft.service.errors.TokenNotFoundException;
import com.esoft.service.errors.UnauthorizedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.security.RandomUtil;

import java.time.Instant;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;
    private final TokenHistoryService tokenHistoryService;
    private final UserMapper userMapper;
    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, JWTUtil jwtUtil,
                                     UserRepository userRepository, TokenHistoryService tokenHistoryService,
                                     UserMapper userMapper) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.tokenHistoryService = tokenHistoryService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public AuthorizationDataDTO createToken(String username, String password) {
        Authentication authentication = authenticateUser(username, password);
        return createToken(authentication);
    }

    private Authentication authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(username, password);

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    @Override
    @Transactional
    public void revokeToken(String token) {
        String hashedToken = jwtUtil.hashToken(token);

        TokenHistory tokenHistory = tokenHistoryService.findOneByHashedToken(hashedToken)
            .orElseThrow(TokenNotFoundException::new);

        if (tokenHistory.getStatus() == TokenStatus.REVOKED) {
            throw new TokenAlreadyRevokedException();
        }

        revokeOldToken(tokenHistory);
        // TODO: revoke any associated refresh tokens
    }

    @Override
    @Transactional(readOnly = true)
    public Result<AuthorizationDataDTO> validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return Result.failure(Constants.AUTH_MESSAGE.TOKEN_INVALID);
        }

        if (!jwtUtil.validateAccessToken(token)) {
            return Result.failure(Constants.AUTH_MESSAGE.TOKEN_INVALID);
        }

        String hashedToken = jwtUtil.hashToken(token);
        Optional<TokenHistory> tokenHistoryOptional = tokenHistoryService.findOneByHashedToken(hashedToken);
        if (tokenHistoryOptional.isEmpty()) {
            return Result.failure(Constants.AUTH_MESSAGE.TOKEN_NOT_FOUND);
        }

        TokenHistory tokenHistory = tokenHistoryOptional.get();
        if (tokenHistory.getStatus() == TokenStatus.REVOKED) {
            return Result.failure(Constants.AUTH_MESSAGE.TOKEN_REVOKED);
        }

        return Result.success(
            new AuthorizationDataDTO(
                userMapper.userToAdminUserDTO(tokenHistory.getUser())),
            Constants.AUTH_MESSAGE.TOKEN_VALID
        );
    }

    @Override
    @Transactional
    public AuthorizationDataDTO refreshToken(String refreshToken) {
        boolean rememberMe = false;
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new UnauthorizedException("Refresh token is invalid");
        }

        String hashedRefreshToken = jwtUtil.hashToken(refreshToken);
        TokenHistory tokenHistory = tokenHistoryService
            .findOneByHashedToken(hashedRefreshToken)
            .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (tokenHistory.getType() != TokenType.REFRESH_TOKEN || tokenHistory.getStatus() != TokenStatus.ACTIVE || tokenHistory.getExpiryDate().isBefore(Instant.now())) {
            throw new UnauthorizedException("Refresh token expired or revoked");
        }

        User user = tokenHistory.getUser();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getAuthorities(), user.getLogin(), null);

        String newAccessToken = jwtUtil.createAccessToken(authentication, rememberMe);
        String newRefreshToken = jwtUtil.createRefreshToken();

        saveAccessToken(newAccessToken, user);
        saveRefreshToken(newRefreshToken, user);
        revokeOldToken(tokenHistory);


        return initAuthorizationData(user, newAccessToken, newRefreshToken);
    }

    @Override
    public AuthorizationDataDTO createToken(Authentication authentication) {
        boolean rememberMe = false;
        User user = userRepository.findOneWithAuthoritiesByLogin(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = jwtUtil.createAccessToken(authentication, rememberMe);
        String refreshTokenValue = jwtUtil.createRefreshToken();

        saveAccessToken(jwt, user);
        saveRefreshToken(refreshTokenValue, user);

        return initAuthorizationData(user, jwt, refreshTokenValue);
    }

    private AuthorizationDataDTO initAuthorizationData(User user, String accessToken, String refreshToken) {
        AuthorizationDataDTO authorizationData = new AuthorizationDataDTO();
        authorizationData.setUser(userMapper.userToAdminUserDTO(user));
        authorizationData.setToken(new TokenResponseDTO(accessToken, refreshToken, jwtUtil.getTokenValidity(false)));
        return authorizationData;
    }


    private void saveAccessToken(String jwt, User user) {
        TokenHistory accessToken = new TokenHistory();
        accessToken.setHashedToken(jwtUtil.hashToken(jwt));
        accessToken.setStatus(TokenStatus.ACTIVE);
        accessToken.setCreatedDate(Instant.now());
        accessToken.setType(TokenType.ACCESS_TOKEN);
        accessToken.setUser(user);

        tokenHistoryService.save(accessToken);
    }

    private void saveRefreshToken(String refreshToken, User user) {
        TokenHistory refreshTokenEntity = new TokenHistory();
        refreshTokenEntity.setHashedToken(jwtUtil.hashToken(refreshToken));
        refreshTokenEntity.setStatus(TokenStatus.ACTIVE);
        refreshTokenEntity.setCreatedDate(Instant.now());
        refreshTokenEntity.setExpiryDate(
            Instant.now().plusSeconds(jwtUtil.getRefreshTokenValidityInSeconds()));
        refreshTokenEntity.setType(TokenType.REFRESH_TOKEN);
        refreshTokenEntity.setUser(user);

        tokenHistoryService.save(refreshTokenEntity);
    }

    private void revokeOldToken(TokenHistory tokenHistory) {
        tokenHistory.setStatus(TokenStatus.REVOKED);
        tokenHistory.setUpdatedDate(Instant.now());
        tokenHistoryService.save(tokenHistory);
    }
}
