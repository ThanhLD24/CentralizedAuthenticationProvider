package com.esoft.web.rest.external;

import com.esoft.service.AuthenticationService;
import com.esoft.service.dto.AuthorizationDTO;
import com.esoft.utils.JWTUtil;
import com.esoft.web.rest.dto.*;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.web.rest.dto.ResponseStatus;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationResource {
    private static final Logger LOG = LoggerFactory.getLogger(AuthenticationResource.class);

    private final AuthenticationService authService;
    private final JWTUtil jwtUtil;
    public AuthenticationResource(AuthenticationService authService, JWTUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/create-token")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> getToken(@Valid @RequestBody AuthRequest request) {
        LOG.info("Creating token for user: {}", request.getUsername());
        TokenResponseDTO tokenResponseDTO = authService.createToken(request.getUsername(), request.getPassword());

        return ResponseEntity.ok(
            ApiResponse.<TokenResponseDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Authentication successful")
                .data(tokenResponseDTO)
                .build()
        );
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponse<TokenResponseDTO>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        LOG.info("Refreshing token for user: {}", jwtUtil.hashToken(request.getRefreshToken()));
        TokenResponseDTO tokenResponseDTO = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(
            ApiResponse.<TokenResponseDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token refreshed successfully")
                .data(tokenResponseDTO)
                .build()
        );
    }

    @GetMapping("/validate-token")
    public ResponseEntity<ApiResponse<AuthorizationDTO>> validateToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        LOG.info("Validating token: {}", jwtUtil.hashToken(token));
        AuthorizationDTO result = authService.validateToken(token);

        if (!result.isAuthorized()) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401 hoặc 403 tùy logic
                .body(new ApiResponse<AuthorizationDTO>(
                    ResponseStatus.ERROR, result.getMessage()
                ));
        }

        return ResponseEntity.ok(
            ApiResponse.<AuthorizationDTO>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token is valid")
                .data(result)
                .build()
        );
    }

    @PostMapping("/disable-token")
    public ResponseEntity<ApiResponse<Void>> disableToken(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }
        String token = authHeader.replace("Bearer ", "");
        LOG.info("Disabling token: {}", jwtUtil.hashToken(token));
        authService.revokeToken(token);
        return ResponseEntity.ok(
            ApiResponse.<Void>builder()
                .status(ResponseStatus.SUCCESS)
                .message("Token has been disabled")
                .build()
        );
    }
}
