package com.esoft.security.oauth2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuth2ProviderFactory {

    private final Map<String, OAuth2Provider> providerMap;

    @Autowired
    public OAuth2ProviderFactory(List<OAuth2Provider> providers) {
        this.providerMap = providers.stream()
            .collect(Collectors.toMap(OAuth2Provider::getProviderName, Function.identity()));
    }

    public OAuth2Provider getProvider(String providerKey) {
        return Optional.ofNullable(providerMap.get(providerKey))
            .orElseThrow(() -> new IllegalArgumentException("Unknown OAuth2 provider: " + providerKey));
    }
}
