package name.nikolaikochkin.ratelimiter.service.limiter;

import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import reactor.core.publisher.Mono;

/**
 * The {@code RateLimitService} interface defines the contract for services
 * that manage rate limiting.
 * <p>Implementations of this interface provide a method to determine whether a request from a specific key
 * should be allowed or denied based on the rate-limiting criteria.</p>
 */
public interface RateLimitService {

    /**
     * Determines whether a request from the specified key should be allowed
     * based on the current rate-limiting criteria.
     * This method should return {@code true} if the key is within their request limit
     * and {@code false} if the key has exceeded their limit.
     *
     * <p>The implementation of this method is responsible for tracking the
     * request counts and applying the rate limiting logic. It should be
     * efficient and accurate to ensure fair and consistent enforcement of
     * rate limits.</p>
     *
     * @param rateLimitKey the {@link RateLimitKey} whose request is to be evaluated
     * @return {@code true} if the request is allowed; {@code false} otherwise
     * @throws IllegalArgumentException if the key is null
     */
    Mono<Boolean> allowRequest(RateLimitKey rateLimitKey);
}
