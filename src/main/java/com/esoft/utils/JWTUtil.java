package com.esoft.utils;

import com.esoft.security.DomainUserDetailsService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.esoft.security.SecurityUtils.*;

@Component
public class JWTUtil {

    @Getter
    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Getter
    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    // get Refresh Token Validity in Seconds
    @Getter
    @Value("${application.refresh-token-validity-in-seconds:0}")
    private long refreshTokenValidityInSeconds;

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    public JWTUtil(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public long getTokenValidity(boolean rememberMe) {
        return rememberMe ? tokenValidityInSecondsForRememberMe : tokenValidityInSeconds;
    }

    public String createAccessToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(" "));

        Instant now = Instant.now();
        Instant validity = now.plusSeconds(getTokenValidity(rememberMe));

        // TODO: set application code in the JWT claims
        JwtClaimsSet.Builder builder = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(validity)
            .subject(authentication.getName())
            .claim(AUTHORITIES_CLAIM, authorities);
        if (authentication.getPrincipal() instanceof DomainUserDetailsService.UserWithId user) {
            builder.claim(USER_ID_CLAIM, user.getId());
        }

        JwsHeader jwsHeader = JwsHeader.with(JWT_ALGORITHM).build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, builder.build())).getTokenValue();
    }

    public boolean validateAccessToken(String token) {
        try {
            Jwt jwt = this.jwtDecoder.decode(token);
            Instant now = Instant.now();
            return jwt.getExpiresAt() != null && now.isBefore(jwt.getExpiresAt());
        } catch (JwtException e) {
            return false;
        }
    }

    public String createRefreshToken() {
        UUID uuid = UUID.randomUUID();
        Instant expiryDate = Instant.now().plusMillis(refreshTokenValidityInSeconds);
        long timestamp = expiryDate.toEpochMilli();
        String rawToken = uuid + ":" + timestamp;
        return Base64.getUrlEncoder().withoutPadding().encodeToString(rawToken.getBytes());
    }

    public String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing token", e);
        }
    }
}
