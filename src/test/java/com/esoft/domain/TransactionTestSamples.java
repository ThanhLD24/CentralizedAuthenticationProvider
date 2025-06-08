package com.esoft.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Transaction getTransactionSample1() {
        return new Transaction()
            .id(1L)
            .action("action1")
            .status(1)
            .message("message1")
            .deviceInfo("deviceInfo1")
            .clientIp("clientIp1")
            .requestPath("requestPath1")
            .requestMethod("requestMethod1")
            .username("username1")
            .userId(1L)
            .duration(1L);
    }

    public static Transaction getTransactionSample2() {
        return new Transaction()
            .id(2L)
            .action("action2")
            .status(2)
            .message("message2")
            .deviceInfo("deviceInfo2")
            .clientIp("clientIp2")
            .requestPath("requestPath2")
            .requestMethod("requestMethod2")
            .username("username2")
            .userId(2L)
            .duration(2L);
    }

    public static Transaction getTransactionRandomSampleGenerator() {
        return new Transaction()
            .id(longCount.incrementAndGet())
            .action(UUID.randomUUID().toString())
            .status(intCount.incrementAndGet())
            .message(UUID.randomUUID().toString())
            .deviceInfo(UUID.randomUUID().toString())
            .clientIp(UUID.randomUUID().toString())
            .requestPath(UUID.randomUUID().toString())
            .requestMethod(UUID.randomUUID().toString())
            .username(UUID.randomUUID().toString())
            .userId(longCount.incrementAndGet())
            .duration(longCount.incrementAndGet());
    }
}
