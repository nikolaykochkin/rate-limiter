package name.nikolaikochkin.ratelimiter.service.limiter;

import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import name.nikolaikochkin.ratelimiter.service.factory.TokenBucketRateLimiterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

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
        Flux<Boolean> flux = Flux.range(0, PERMITS)
                .flatMap(i -> rateLimitService.allowRequest(new Key("1.1.1.1")))
                .filter(Boolean::booleanValue);

        StepVerifier.create(flux)
                .expectNextCount(PERMITS)
                .verifyComplete();
    }

    @Test
    void denyClientRequestAfterLimitExceeded() {
        List<Boolean> results = Flux.range(0, PERMITS)
                .flatMap(i -> rateLimitService.allowRequest(new Key("1.1.1.1")))
                .collectList()
                .block();

        StepVerifier.create(rateLimitService.allowRequest(new Key("1.1.1.1")))
                .expectNext(false)
                .verifyComplete();

        assertNotNull(results);
        assertEquals(results.size(), PERMITS);
        assertTrue(results.stream().allMatch(Boolean::booleanValue));
    }

    @Test
    void allowMultipleClientMultipleRequestsConcurrent() throws InterruptedException {
        final int numberOfThreads = 10;
        final ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        final AtomicInteger allowedRequests = new AtomicInteger();

        for (int i = 0; i < numberOfThreads; i++) {
            final Key key = new Key("1.1.1." + i);
            executorService.execute(() -> {
                for (int j = 0; j < PERMITS; j++) {
                    if (Boolean.TRUE.equals(rateLimitService.allowRequest(key).block())) {
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
                if (Boolean.TRUE.equals(rateLimitService.allowRequest(new Key("1.1.1.1")).block())) {
                    allowedRequests.incrementAndGet();
                }
            });
        }

        executorService.shutdown();
        assertTrue(executorService.awaitTermination(1, TimeUnit.SECONDS));
        assertEquals(PERMITS, allowedRequests.get());
    }
}