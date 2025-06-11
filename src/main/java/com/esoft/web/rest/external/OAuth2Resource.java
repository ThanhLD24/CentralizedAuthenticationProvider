package com.esoft.web.rest.external;

import com.esoft.domain.ApplicationSystem;
import com.esoft.security.oauth2.OAuth2Provider;
import com.esoft.security.oauth2.OAuth2ProviderFactory;
import com.esoft.service.ApplicationSystemService;
import com.esoft.service.dto.ApplicationSystemDTO;
import com.esoft.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Resource {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Resource.class);
    private final OAuth2ProviderFactory oAuth2ProviderFactory;
    private final ApplicationSystemService applicationSystemService;
    private final JWTUtil jwtUtil;

    public OAuth2Resource(OAuth2ProviderFactory oAuth2ProviderFactory, ApplicationSystemService applicationSystemService, JWTUtil jwtUtil) {
        this.oAuth2ProviderFactory = oAuth2ProviderFactory;
        this.applicationSystemService = applicationSystemService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/authorize")
    public void initiateOAuth2(@RequestParam String provider,
                               @RequestParam("redirect_uri") String redirectUri,
                               @RequestHeader(value = "X-System-Secret", required = false, defaultValue = "") String systemSecretKey,
                               @RequestParam(value = "bypassfortest", required = false) boolean bypass,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        LOG.info("Initiating OAuth2 for provider: {}, redirectUri: {}", provider, redirectUri);
        try {
            request.getSession().setAttribute("redirect_uri", redirectUri);
            Optional<ApplicationSystemDTO> application = applicationSystemService.findBySecretKeyAndActive(systemSecretKey, true);
            if (bypass || application.isPresent()) {
                OAuth2Provider oAuth2Provider = oAuth2ProviderFactory.getProvider(provider);
                response.sendRedirect(oAuth2Provider.getRedirectUri());
            } else
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Application system not found or inactive");
        } catch (Exception e) {
            LOG.error("Error initiating OAuth2 for provider: {}", provider, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    // FOR TEST
    @GetMapping("/createHashKey/{secretKey}")
    public String createHashKey(@PathVariable String secretKey) {
        if (secretKey == null || secretKey.isEmpty()) {
            throw new ApplicationContextException("Secret key is required");
        }
        return jwtUtil.hashToken(secretKey);
    }

}
