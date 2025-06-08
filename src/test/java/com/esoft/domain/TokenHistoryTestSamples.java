package com.esoft.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TokenHistoryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TokenHistory getTokenHistorySample1() {
        return new TokenHistory().id(1L).hashedToken("hashedToken1");
    }

    public static TokenHistory getTokenHistorySample2() {
        return new TokenHistory().id(2L).hashedToken("hashedToken2");
    }

    public static TokenHistory getTokenHistoryRandomSampleGenerator() {
        return new TokenHistory().id(longCount.incrementAndGet()).hashedToken(UUID.randomUUID().toString());
    }
}
