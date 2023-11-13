package name.nikolaikochkin.ratelimiter.service.limiter;

import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import name.nikolaikochkin.ratelimiter.service.factory.TokenBucketRateLimiterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryRateLimitServiceTest {
    private static final int PERMITS = 20;
    private static final Duration PERIOD = Duration.ofSeconds(1);
    private static final RateLimiterFactory FACTORY = new TokenBucketRateLimiterFactory(PERMITS, PERIOD);

    private record Key(String s) implements RateLimitKey {
    }

    private RateLimitService rateLimitService;

    @BeforeEach
    void setUp() {
        rateLimitService = new InMemoryRateLimitService(FACTORY);
    }

    @Test
    void allowClientSingleRequest() {
        StepVerifier.create(rateLimitService.allowRequest(new Key("1.1.1.1")))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void allowClientMultipleRequests() {
        StepVerifier.create(rateLimitService.allowRequest(new Key("1.1.1.1")), PERMITS + 1)
                .expectNextMatches(Boolean::booleanValue)
                .verifyComplete();
    }
//
//    @Test
//    void denyClientRequestAfterLimitExceeded() {
//        for (int i = 0; i < PERMITS; i++) {
//            rateLimitService.allowClientRequest(new Client("1.1.1.1"));
//        }
//        assertFalse(rateLimitService.allowClientRequest(new Client("1.1.1.1")));
//    }
//
//    @Test
//    void allowMultipleClientMultipleRequestsConcurrent() throws InterruptedException {
//        final int numberOfThreads = 10;
//        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
//        final AtomicInteger allowedRequests = new AtomicInteger();
//
//        for (int i = 0; i < numberOfThreads; i++) {
//            final Client client = new Client("1.1.1." + i);
//            executorService.execute(() -> {
//                for (int j = 0; j < PERMITS; j++) {
//                    if (rateLimitService.allowClientRequest(client)) {
//                        allowedRequests.incrementAndGet();
//                    }
//                }
//            });
//        }
//
//        executorService.shutdown();
//        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
//        assertEquals(numberOfThreads * PERMITS, allowedRequests.get());
//    }
//
//    @Test
//    void allowSingleClientMultipleRequestsConcurrent() throws InterruptedException {
//        final ExecutorService executorService = Executors.newFixedThreadPool(PERMITS);
//        final AtomicInteger allowedRequests = new AtomicInteger();
//
//        for (int i = 0; i < PERMITS; i++) {
//            executorService.execute(() -> {
//                if (rateLimitService.allowClientRequest(new Client("1.1.1.1"))) {
//                    allowedRequests.incrementAndGet();
//                }
//            });
//        }
//
//        executorService.shutdown();
//        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
//        assertEquals(PERMITS, allowedRequests.get());
//    }
}