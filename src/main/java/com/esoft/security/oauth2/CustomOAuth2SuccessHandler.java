package com.esoft.security.oauth2;

import com.esoft.domain.User;
import com.esoft.repository.UserRepository;
import com.esoft.service.AuthenticationService;
import com.esoft.service.UserInternalService;
import com.esoft.service.dto.AdminUserDTO;
import com.esoft.service.dto.AuthorizationDataDTO;
import com.esoft.service.dto.TokenResponseDTO;
import com.esoft.service.errors.UnauthorizedException;
import com.esoft.utils.JWTUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tech.jhipster.security.RandomUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;
    private final UserInternalService userInternalService;
    private final OAuth2ProviderFactory oauth2ProviderFactory;

    public CustomOAuth2SuccessHandler(AuthenticationService authenticationService, @Lazy UserInternalService userInternalService, OAuth2ProviderFactory oauth2ProviderFactory) {
        this.authenticationService = authenticationService;
        this.userInternalService = userInternalService;
        this.oauth2ProviderFactory = oauth2ProviderFactory;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = oauthToken.getPrincipal();
        String provider = oauthToken.getAuthorizedClientRegistrationId();

        User appUser = registerUser(user, provider);
        if (!appUser.isActivated()) {
            throw new UnauthorizedException("User account is not activated");
        }

        List<GrantedAuthority> grantedAuthorities = appUser.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .collect(Collectors.toList());
        UsernamePasswordAuthenticationToken localAuthentication = new UsernamePasswordAuthenticationToken(
            appUser.getLogin(), null, grantedAuthorities);
        AuthorizationDataDTO authorizationData = authenticationService.createToken(localAuthentication);
        TokenResponseDTO tokenPair = authorizationData.getToken();
        HttpSession session = request.getSession();
        String redirectUri = (String) session.getAttribute("redirect_uri");
        session.removeAttribute("redirect_uri");

        response.sendRedirect(redirectUri + "?token=" + tokenPair.getAccessToken() +
                "&refreshToken=" + tokenPair.getRefreshToken() +
                "&expiresIn=" + tokenPair.getExpiresIn());
    }

    private User registerUser(OAuth2User oAuth2User, String provider) {
        OAuth2Provider oAuth2Provider = oauth2ProviderFactory.getProvider(provider);
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (email == null || email.isEmpty()) {
            throw new UnauthorizedException("Email is required for OAuth2 authentication");
        }

        Optional<User> appUser = userInternalService.getUserWithAuthoritiesByLogin(email);
        if (appUser.isPresent()) {
            return appUser.get();
        }

        AdminUserDTO adminUserDTO = new AdminUserDTO();
        adminUserDTO.setEmail(email);
        adminUserDTO.setLogin(email);
        adminUserDTO.setFirstName(name);
        adminUserDTO.setProvider(provider);
        adminUserDTO.setActivated(true);
        adminUserDTO.setLangKey("en");
        adminUserDTO.setAuthorities(oAuth2Provider.getMappedAuthorities(oAuth2User));

        return userInternalService.createUser(adminUserDTO);
        //TODO: send email notification for new user registration if needed
    }
}
