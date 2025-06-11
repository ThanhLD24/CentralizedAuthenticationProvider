package com.esoft.security.oauth2;

import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Set;

public interface OAuth2Provider {
    String getEmail(OAuth2User user);
    String getName(OAuth2User user);
    String getProviderName();
    String getRedirectUri();
    Set<String> getScopes();
    Set<String> getMappedAuthorities(OAuth2User user);
}
