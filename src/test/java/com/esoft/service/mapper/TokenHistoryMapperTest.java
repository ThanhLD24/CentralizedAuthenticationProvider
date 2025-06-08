package com.esoft.service.mapper;

import static com.esoft.domain.TokenHistoryAsserts.*;
import static com.esoft.domain.TokenHistoryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TokenHistoryMapperTest {

    private TokenHistoryMapper tokenHistoryMapper;

    @BeforeEach
    void setUp() {
        tokenHistoryMapper = new TokenHistoryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTokenHistorySample1();
        var actual = tokenHistoryMapper.toEntity(tokenHistoryMapper.toDto(expected));
        assertTokenHistoryAllPropertiesEquals(expected, actual);
    }
}
