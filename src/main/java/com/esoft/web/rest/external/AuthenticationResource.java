package com.esoft.web.rest.external;

import com.esoft.service.AuthenticationService;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.web.rest.LoginResource;
import com.esoft.web.rest.dto.ApiResponse;
import com.esoft.web.rest.dto.AuthRequest;
import com.esoft.web.rest.dto.RefreshTokenRequest;
import com.esoft.web.rest.dto.ResponseStatus;
import com.esoft.service.dto.TokenResponseDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {
    private static final Logger LOG = LoggerFactory.getLogger(LoginResource.class);

    private final JwtEncoder jwtEncoder;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds:0}")
    private long tokenValidityInSeconds;

    @Value("${jhipster.security.authentication.jwt.token-validity-in-seconds-for-remember-me:0}")
    private long tokenValidityInSecondsForRememberMe;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    // inject authentication service
    private final AuthenticationService authService;
    public AuthenticationResource(JwtEncoder jwtEncoder, AuthenticationManagerBuilder authenticationManagerBuilder, AuthenticationService authService) {
        this.jwtEncoder = jwtEncoder;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.authService = authService;
    }

    @PostMapping("/token")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> getToken(@Valid @RequestBody AuthRequest request) {
        TokenResponseDTO tokenResponseDTO = authService.getToken(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(
            ApiResponse.<TokenResponseDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Authentication successful")
                .data(tokenResponseDTO)
                .build()
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponseDTO tokenResponseDTO = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(
            ApiResponse.<TokenResponseDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token refreshed successfully")
                .data(tokenResponseDTO)
                .build()
        );
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<AuthorizationDTO>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        AuthorizationDTO authorizationDTO = authService.validateToken(token);
        return ResponseEntity.ok(
            ApiResponse.<AuthorizationDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token is valid")
                .data(authorizationDTO)
                .build()
        );
    }

    @PostMapping("/disable")
    public ResponseEntity<ApiResponse<Void>> disableToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        authService.revokeToken(token);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token has been disabled")
                .build()
        );
    }
}
