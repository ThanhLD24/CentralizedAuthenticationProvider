package com.esoft.service.impl;

import com.esoft.domain.TokenHistory;
import com.esoft.domain.User;
import com.esoft.domain.enumeration.TokenStatus;
import com.esoft.domain.enumeration.TokenType;
import com.esoft.repository.UserRepository;
import com.esoft.service.TokenHistoryService;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.service.mapper.UserMapper;
import com.esoft.utils.JWTUtil;
import com.esoft.service.errors.TokenAlreadyRevokedException;
import com.esoft.service.errors.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTUtil jwtUtil;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenHistoryService tokenHistoryService;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
    }

    @Test
    void createToken_shouldReturnTokenResponseDTO_whenValidCredentials() {
        String username = "thanhld";
        String password = "1234";
        boolean rememberMe = false;

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(username, password);

        Authentication authentication = mock(Authentication.class);
        User user = new User();
        user.setLogin(username);
        user.setId(1L);

        when(authenticationManager.authenticate(authToken)).thenReturn(authentication);
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findOneWithAuthoritiesByLogin(username)).thenReturn(Optional.of(user));
        when(jwtUtil.createToken(authentication, rememberMe)).thenReturn("jwt-token");
        when(jwtUtil.createRefreshToken()).thenReturn("refresh-token");
        when(jwtUtil.hashToken(anyString())).thenReturn("hashed");

        TokenResponseDTO response = authenticationService.createToken(username, password);

        assertEquals("jwt-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        verify(tokenHistoryService, times(2)).save(any(TokenHistory.class));
    }

    @Test
    void createToken_shouldThrowException_whenUserNotFound() {
        String username = "notfound";
        String password = "pass";
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getName()).thenReturn(username);
        when(userRepository.findOneWithAuthoritiesByLogin(username)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
            () -> authenticationService.createToken(username, password));
    }

    @Test
    void revokeToken_shouldThrow_whenTokenAlreadyRevoked() {
        String token = "revoked-token";
        TokenHistory tokenHistory = new TokenHistory();
        tokenHistory.setStatus(TokenStatus.REVOKED);

        when(jwtUtil.hashToken(token)).thenReturn("hashed-token");
        when(tokenHistoryService.findOneByHashedToken("hashed-token"))
            .thenReturn(Optional.of(tokenHistory));

        assertThrows(TokenAlreadyRevokedException.class, () ->
            authenticationService.revokeToken(token));
    }

    @Test
    void validateToken_shouldReturnFalse_whenTokenEmpty() {
        AuthorizationDTO result = authenticationService.validateToken("");
        assertFalse(result.isAuthorized());
    }

    @Test
    void refreshToken_shouldThrow_whenRefreshTokenInvalid() {
        assertThrows(UnauthorizedException.class, () ->
            authenticationService.refreshToken(""));
    }

    @Test
    void refreshToken_shouldReturnNewToken_whenValid() {
        String refreshToken = "refreshToken";
        String hashedRefreshToken = "hashed-token";
        User user = new User();
        user.setLogin("admin");

        TokenHistory th = new TokenHistory();
        th.setType(TokenType.REFRESH_TOKEN);
        th.setStatus(TokenStatus.ACTIVE);
        th.setExpiryDate(Instant.now().plusSeconds(60));
        th.setUser(user);

        when(jwtUtil.hashToken(refreshToken)).thenReturn(hashedRefreshToken);
        when(tokenHistoryService.findOneByHashedToken(hashedRefreshToken)).thenReturn(Optional.of(th));
        when(jwtUtil.createToken(any(), eq(false))).thenReturn("new-access-token");
        when(jwtUtil.createRefreshToken()).thenReturn("new-refresh-token");

        TokenResponseDTO dto = authenticationService.refreshToken(refreshToken);

        assertEquals("new-access-token", dto.getAccessToken());
        assertEquals("new-refresh-token", dto.getRefreshToken());
    }
}
