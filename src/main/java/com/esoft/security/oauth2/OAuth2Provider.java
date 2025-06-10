package com.esoft.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Provider {
    String getEmail(OAuth2User user);
    String getName(OAuth2User user);
    String getProviderName();
    String getRedirectUri();
}
