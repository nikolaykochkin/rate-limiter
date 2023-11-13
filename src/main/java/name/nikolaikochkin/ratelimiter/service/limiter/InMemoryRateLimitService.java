package name.nikolaikochkin.ratelimiter.service.limiter;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The {@code InMemoryRateLimitService} class implements the {@link RateLimitService} interface,
 * providing an in-memory rate limiting mechanism.
 * <p>It uses a {@link ConcurrentMap} to store and manage {@link RateLimiter} instances for each {@link Client}.</p>
 * <p>This service delegates the creation of {@link RateLimiter} instances to a {@link RateLimiterFactory}.</p>
 *
 * @see RateLimitService
 * @see RateLimiterFactory
 * @see RateLimiter
 * @see Client
 */
@Slf4j
@Service
public class InMemoryRateLimitService implements RateLimitService {

    private final RateLimiterFactory rateLimiterFactory;
    private final ConcurrentMap<RateLimitKey, RateLimiter> buckets;

    /**
     * Constructs a new {@code InMemoryRateLimitService} with the specified {@link RateLimiterFactory}.
     *
     * @param rateLimiterFactory the factory to create {@link RateLimiter} instances
     */
    public InMemoryRateLimitService(RateLimiterFactory rateLimiterFactory) {
        this.rateLimiterFactory = rateLimiterFactory;
        this.buckets = new ConcurrentHashMap<>();
    }

    /**
     * Evaluates if a request from the specified client should be allowed.
     * This method retrieves or creates a {@link RateLimiter} for the client
     * and then checks if a request can be made under the current rate limit.
     *
     * @param client the {@link Client} whose request is to be evaluated
     * @return {@code true} if the request is within the rate limit and can be allowed; {@code false} otherwise
     * @throws IllegalArgumentException if the client is null
     */
    @Override
    public Mono<Boolean> allowRequest(RateLimitKey rateLimitKey) {
        Assert.notNull(rateLimitKey, "Key must not be null");
        RateLimiter rateLimiter = buckets.computeIfAbsent(rateLimitKey, key -> rateLimiterFactory.createRateLimiter());
        log.debug("Key: {}, has limits: {}", rateLimitKey, rateLimiter);
        return Mono.fromCallable(rateLimiter::tryConsume);
    }
}
