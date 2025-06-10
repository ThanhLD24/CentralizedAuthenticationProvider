package com.esoft.web.rest.external;

import com.esoft.security.oauth2.OAuth2Provider;
import com.esoft.security.oauth2.OAuth2ProviderFactory;
import com.esoft.service.ApplicationSystemService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2Resource {

    private static final Logger LOG = LoggerFactory.getLogger(OAuth2Resource.class);
    private final OAuth2ProviderFactory oAuth2ProviderFactory;
    private final ApplicationSystemService applicationSystemService;

    public OAuth2Resource(OAuth2ProviderFactory oAuth2ProviderFactory, ApplicationSystemService applicationSystemService) {
        this.oAuth2ProviderFactory = oAuth2ProviderFactory;
        this.applicationSystemService = applicationSystemService;
    }

    @GetMapping("/authorize")
    public void initiateOAuth2(@RequestParam String provider,
                               @RequestParam("redirect_uri") String redirectUri,
                               @RequestHeader(value = "X-System-Secret") String systemSecretKey,
                               HttpServletRequest request,
                               HttpServletResponse response) throws IOException {
        LOG.info("Initiating OAuth2 for provider: {}, redirectUri: {}", provider, redirectUri);
        try {
            request.getSession().setAttribute("redirect_uri", redirectUri);
//            applicationSystemService.findOne(systemSecretKey);
            OAuth2Provider oAuth2Provider = oAuth2ProviderFactory.getProvider(provider);
            response.sendRedirect(oAuth2Provider.getRedirectUri());
        } catch (Exception e) {
            LOG.error("Error initiating OAuth2 for provider: {}", provider, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/test")
    public void initiateOAuthTest() {
        LOG.info("initiateOAuthTest");
    }
}
