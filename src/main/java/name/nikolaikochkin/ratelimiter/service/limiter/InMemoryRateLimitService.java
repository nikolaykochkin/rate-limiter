package name.nikolaikochkin.ratelimiter.service.limiter;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The {@code InMemoryRateLimitService} class implements the {@link RateLimitService} interface,
 * providing an in-memory rate limiting mechanism.
 * <p>It uses a {@link ConcurrentMap} to store and manage {@link RateLimiter} instances for each {@link RateLimitKey}.</p>
 * <p>This service delegates the creation of {@link RateLimiter} instances to a {@link RateLimiterFactory}.</p>
 *
 * @see RateLimitService
 * @see RateLimiterFactory
 * @see RateLimiter
 * @see RateLimitKey
 */
@Slf4j
@Service
@ToString
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
     * Evaluates if a request from the specified key should be allowed.
     * This method retrieves or creates a {@link RateLimiter} for the key
     * and then checks if a request can be made under the current rate limit.
     *
     * @param rateLimitKey the {@link RateLimitKey} whose request is to be evaluated
     * @return {@code true} if the request is within the rate limit and can be allowed; {@code false} otherwise
     * @throws IllegalArgumentException if the key is null
     */
    @Override
    public Mono<Boolean> allowRequest(RateLimitKey rateLimitKey) {
        if (Objects.isNull(rateLimitKey)) {
            return Mono.error(new IllegalArgumentException("Key must not be null"));
        }
        return Mono.fromCallable(() -> buckets.computeIfAbsent(rateLimitKey, key -> rateLimiterFactory.createRateLimiter()))
                .doOnNext(rateLimiter -> log.debug("Key: {}, has limits: {}", rateLimitKey, rateLimiter))
                .map(RateLimiter::tryConsume);
    }
}
