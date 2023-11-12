package name.nikolaikochkin.ratelimiter.service.factory;

import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;

/**
 * The {@code RateLimiterFactory} interface defines the contract for factory classes
 * that create instances of {@link RateLimiter}.
 */
public interface RateLimiterFactory {
    /**
     * Creates and returns a new instance of a {@link RateLimiter}.
     *
     * @return a new instance of {@link RateLimiter}, configured and ready for use
     */
    RateLimiter createRateLimiter();
}
