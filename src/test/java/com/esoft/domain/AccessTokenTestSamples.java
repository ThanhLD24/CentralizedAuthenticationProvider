package com.esoft.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AccessTokenTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static AccessToken getAccessTokenSample1() {
        return new AccessToken().id(1L).hashedToken("hashedToken1");
    }

    public static AccessToken getAccessTokenSample2() {
        return new AccessToken().id(2L).hashedToken("hashedToken2");
    }

    public static AccessToken getAccessTokenRandomSampleGenerator() {
        return new AccessToken().id(longCount.incrementAndGet()).hashedToken(UUID.randomUUID().toString());
    }
}
