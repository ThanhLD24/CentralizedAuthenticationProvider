package com.esoft.service.mapper;

import static com.esoft.domain.ApplicationSystemAsserts.*;
import static com.esoft.domain.ApplicationSystemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ApplicationSystemMapperTest {

    private ApplicationSystemMapper applicationSystemMapper;

    @BeforeEach
    void setUp() {
        applicationSystemMapper = new ApplicationSystemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getApplicationSystemSample1();
        var actual = applicationSystemMapper.toEntity(applicationSystemMapper.toDto(expected));
        assertApplicationSystemAllPropertiesEquals(expected, actual);
    }
}
