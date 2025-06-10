package com.esoft.security.oauth2;

import com.esoft.domain.User;
import com.esoft.repository.UserRepository;
import com.esoft.service.AuthenticationService;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.utils.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    public CustomOAuth2SuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        TokenResponseDTO tokenPair = authenticationService.createTokenFromOAuth2(oauthToken);

        HttpSession session = request.getSession();
        String redirectUri = (String) session.getAttribute("redirect_uri");
        session.removeAttribute("redirect_uri");

        response.sendRedirect(redirectUri + "?token=" + tokenPair.getAccessToken() +
                "&refreshToken=" + tokenPair.getRefreshToken() +
                "&expiresIn=" + tokenPair.getExpiresIn());
    }
}
