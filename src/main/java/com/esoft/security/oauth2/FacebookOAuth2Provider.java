package com.esoft.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

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
}
