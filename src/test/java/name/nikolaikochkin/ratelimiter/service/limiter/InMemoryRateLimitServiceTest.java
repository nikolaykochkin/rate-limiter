package name.nikolaikochkin.ratelimiter.service.limiter;

import name.nikolaikochkin.ratelimiter.model.Client;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import name.nikolaikochkin.ratelimiter.service.factory.TokenBucketRateLimiterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimitServiceTest {
    private static final int PERMITS = 20;
    private static final Duration PERIOD = Duration.ofSeconds(1);
    private static final RateLimiterFactory FACTORY = new TokenBucketRateLimiterFactory(PERMITS, PERIOD);

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new InMemoryRateLimitService(FACTORY);
    }

    @Test
    void allowClientSingleRequest() {
        Client client = new Client("1.1.1.1");
        assertTrue(rateLimitService.allowClientRequest(client));
    }

    @Test
    void allowClientMultipleRequests() {
        for (int i = 0; i < PERMITS; i++) {
            assertTrue(rateLimitService.allowClientRequest(new Client("1.1.1.1")));
        }
    }

    @Test
    void denyClientRequestAfterLimitExceeded() {
        for (int i = 0; i < PERMITS; i++) {
            rateLimitService.allowClientRequest(new Client("1.1.1.1"));
        }
        assertFalse(rateLimitService.allowClientRequest(new Client("1.1.1.1")));
    }

    @Test
    void allowMultipleClientMultipleRequestsConcurrent() throws InterruptedException {
        final int numberOfThreads = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger allowedRequests = new AtomicInteger();

        for (int i = 0; i < numberOfThreads; i++) {
            final Client client = new Client("1.1.1." + i);
            executorService.execute(() -> {
                for (int j = 0; j < PERMITS; j++) {
                    if (rateLimitService.allowClientRequest(client)) {
                        allowedRequests.incrementAndGet();
                    }
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(numberOfThreads * PERMITS, allowedRequests.get());
    }

    @Test
    void allowSingleClientMultipleRequestsConcurrent() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(PERMITS);
        final AtomicInteger allowedRequests = new AtomicInteger();

        for (int i = 0; i < PERMITS; i++) {
            executorService.execute(() -> {
                if (rateLimitService.allowClientRequest(new Client("1.1.1.1"))) {
                    allowedRequests.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(PERMITS, allowedRequests.get());
    }
}