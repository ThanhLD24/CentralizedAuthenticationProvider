package com.esoft.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component("facebook")
public class FacebookOAuth2Provider implements OAuth2Provider {

    @Override
    public String getEmail(OAuth2User user) {
        return user.getAttribute("email");
    }

    @Override
    public String getName(OAuth2User user) {
        return user.getAttribute("name");
    }

    @Override
    public String getProviderName() {
        return "facebook";
    }
    @Override
    public String getRedirectUri() {
        return "/oauth2/authorization/" + getProviderName();
    }

    @Override
    public Set<String> getScopes() {
        return null;
    }

    @Override
    public Set<String> getMappedAuthorities(OAuth2User user) {
        // TODO: convert Facebook user attributes to authorities
        // temporarily returning ROLE_USER
        return Set.of("ROLE_USER");
    }
}
