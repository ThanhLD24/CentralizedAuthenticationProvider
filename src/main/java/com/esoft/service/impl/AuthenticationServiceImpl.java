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
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;
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
    private final UserInternalService userInternalService;
    private final TokenHistoryService tokenHistoryService;
    private final UserMapper userMapper;
    public AuthenticationServiceImpl(AuthenticationManagerBuilder authenticationManagerBuilder, JWTUtil jwtUtil,
                                     UserRepository userRepository, UserInternalService userInternalService, TokenHistoryService tokenHistoryService,
                                     UserMapper userMapper) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.userInternalService = userInternalService;
        this.tokenHistoryService = tokenHistoryService;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public TokenResponseDTO createToken(String username, String password) {
        boolean rememberMe = false;
        Authentication authentication = authenticateUser(username, password);
        return createTokenByAuthentication(authentication, rememberMe);
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
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorizationDTO validateToken(String token) {
        if (token == null || token.isEmpty()) {
            return new AuthorizationDTO(false, Constants.AUTH_MESSAGE.TOKEN_INVALID, null);
        }

        if (!jwtUtil.validateAccessToken(token)) {
            return new AuthorizationDTO(false, Constants.AUTH_MESSAGE.TOKEN_INVALID, null);
        }

        String hashedToken = jwtUtil.hashToken(token);
        Optional<TokenHistory> tokenHistoryOptional = tokenHistoryService.findOneByHashedToken(hashedToken);

        if (tokenHistoryOptional.isEmpty()) {
            return new AuthorizationDTO(false, Constants.AUTH_MESSAGE.TOKEN_INVALID, null);
        }

        TokenHistory tokenHistory = tokenHistoryOptional.get();

        if (tokenHistory.getStatus() != TokenStatus.ACTIVE) {
            return new AuthorizationDTO(false, Constants.AUTH_MESSAGE.TOKEN_REVOKED, null);
        }

        AdminUserDTO authorizationData = userMapper.userToAdminUserDTO(tokenHistory.getUser());
        return new AuthorizationDTO(true, Constants.AUTH_MESSAGE.TOKEN_VALID, authorizationData);
    }

    @Override
    @Transactional
    public TokenResponseDTO refreshToken(String refreshToken) {
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

        String newAccessToken = jwtUtil.createAccessToken(authentication, false);
        String newRefreshToken = jwtUtil.createRefreshToken();

        saveAccessToken(newAccessToken, user);
        saveRefreshToken(newRefreshToken, user);
        revokeOldToken(tokenHistory);
        return new TokenResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public TokenResponseDTO createTokenFromOAuth2(OAuth2AuthenticationToken authenticationToken) {
        OAuth2AuthenticationToken oauthToken = authenticationToken;
        OAuth2User user = oauthToken.getPrincipal();

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String provider = oauthToken.getAuthorizedClientRegistrationId();
        if (email == null || email.isEmpty()) {
            throw new UnauthorizedException("Email is required for OAuth2 authentication");
        }
        handleRegisterUser(email, name, provider);
        return createTokenByAuthentication(authenticationToken, false);
    }

    private TokenResponseDTO createTokenByAuthentication(Authentication authentication, boolean rememberMe) {
        User user = userRepository.findOneWithAuthoritiesByLogin(authentication.getName())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwt = jwtUtil.createAccessToken(authentication, rememberMe);
        String refreshTokenValue = jwtUtil.createRefreshToken();

        saveAccessToken(jwt, user);
        saveRefreshToken(refreshTokenValue, user);

        return new TokenResponseDTO(jwt, refreshTokenValue, jwtUtil.getTokenValidity(rememberMe));
    }

    private void handleRegisterUser(String email, String name, String provider) {
        Optional<User> appUser = userRepository.findOneByLogin(email);
        if (appUser.isEmpty()) {

            AdminUserDTO adminUserDTO = new AdminUserDTO();
            adminUserDTO.setEmail(email);
            adminUserDTO.setLogin(email);
            adminUserDTO.setFirstName(name);
//            adminUserDTO.setProvider(provider);
            adminUserDTO.setActivated(true);
            adminUserDTO.setLangKey("en");

            userInternalService.registerUser(adminUserDTO, RandomUtil.generatePassword());
            //TODO: send email notification for new user registration
        }
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
