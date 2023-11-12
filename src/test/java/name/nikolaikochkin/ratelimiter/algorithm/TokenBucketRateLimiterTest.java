package name.nikolaikochkin.ratelimiter.algorithm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class TokenBucketRateLimiterTest {
    private static final int PERMITS = 20;
    private static final Duration PERIOD = Duration.ofSeconds(1);

    private RateLimiter rateLimiter;


    @BeforeEach
    void setUp() {
        rateLimiter = new TokenBucketRateLimiter(PERMITS, PERIOD);
    }

    @Test
    void tryConsumeOne() {
        assertTrue(rateLimiter.tryConsume(1));
    }

    @Test
    void tryConsumeMany() {
        assertTrue(rateLimiter.tryConsume(PERMITS));
    }

    @Test
    void tryConsumeManyInCycle() {
        for (int i = 0; i < PERMITS; i++) {
            assertTrue(rateLimiter.tryConsume(1));
        }
    }

    @Test
    void tryConsumeOverLimit() {
        assertFalse(rateLimiter.tryConsume(PERMITS + 1));
    }

    @Test
    void tryConsumeZero() {
        assertThrows(AssertionError.class, () -> rateLimiter.tryConsume(0));
    }

    @Test
    void tryConsumeNegative() {
        assertThrows(AssertionError.class, () -> rateLimiter.tryConsume(-1));
    }

    @Test
    void tryConsumeRefill() throws InterruptedException {
        assertTrue(rateLimiter.tryConsume(PERMITS));
        assertFalse(rateLimiter.tryConsume(PERMITS));
        Thread.sleep(PERIOD.toMillis());
        assertTrue(rateLimiter.tryConsume(PERMITS));
        assertFalse(rateLimiter.tryConsume(PERMITS));
    }

    @Test
    void tryConsumeConcurrent() throws InterruptedException {
        final int numberOfThreads = PERMITS * 2;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger successfulConsumptions = new AtomicInteger();

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.execute(() -> {
                if (rateLimiter.tryConsume(1)) {
                    successfulConsumptions.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(PERMITS, successfulConsumptions.get());
    }

    @Test
    void tryConsumeConcurrentRefill() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(PERMITS);
        final AtomicInteger successfulConsumptions = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(PERMITS);

        // Consume initial tokens
        for (int i = 0; i < PERMITS; i++) {
            executorService.execute(() -> {
                rateLimiter.tryConsume(1);
                latch.countDown();
            });
        }

        // Wait for tokens to refill
        latch.await();
        Thread.sleep(PERIOD.toMillis());

        // Attempt to consume tokens again
        for (int i = 0; i < PERMITS; i++) {
            executorService.execute(() -> {
                if (rateLimiter.tryConsume(1)) {
                    successfulConsumptions.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(PERMITS, successfulConsumptions.get());
    }
}