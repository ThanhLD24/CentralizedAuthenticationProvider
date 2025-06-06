package com.esoft.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationSystemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ApplicationSystem getApplicationSystemSample1() {
        return new ApplicationSystem().id(1L).name("name1").description("description1");
    }

    public static ApplicationSystem getApplicationSystemSample2() {
        return new ApplicationSystem().id(2L).name("name2").description("description2");
    }

    public static ApplicationSystem getApplicationSystemRandomSampleGenerator() {
        return new ApplicationSystem()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
