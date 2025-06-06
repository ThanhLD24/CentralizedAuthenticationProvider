package com.esoft.service.mapper;

import static com.esoft.domain.AccessTokenAsserts.*;
import static com.esoft.domain.AccessTokenTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessTokenMapperTest {

    private AccessTokenMapper accessTokenMapper;

    @BeforeEach
    void setUp() {
        accessTokenMapper = new AccessTokenMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAccessTokenSample1();
        var actual = accessTokenMapper.toEntity(accessTokenMapper.toDto(expected));
        assertAccessTokenAllPropertiesEquals(expected, actual);
    }
}
