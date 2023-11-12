package name.nikolaikochkin.ratelimiter.service.factory;

import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.algorithm.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;

/**
 * The {@code TokenBucketRateLimiterFactory} class is an implementation of the {@link RateLimiterFactory}
 * interface that provides a factory method for creating instances of {@link TokenBucketRateLimiter}.
 * This factory is configured with a specific number of permits and a time period, which are used
 * to configure the {@link TokenBucketRateLimiter} instances it creates.
 *
 * @see RateLimiterFactory
 * @see TokenBucketRateLimiter
 */
@Service
public class TokenBucketRateLimiterFactory implements RateLimiterFactory {
    private final long permits;
    private final Duration period;

    /**
     * Constructs a new {@code TokenBucketRateLimiterFactory} with the specified number of permits
     * and refill period.
     *
     * @param permits the number of permits (requests) allowed per period. This value must be positive.
     * @param period  the duration of the time during which all permits will be refilled. This value must be positive.
     * @throws IllegalArgumentException if either permits is non-positive or period is null or non-positive
     */
    public TokenBucketRateLimiterFactory(@Value("${application.ratelimit.permits}") long permits,
                                         @Value("${application.ratelimit.period}") Duration period) {
        Assert.state(permits > 0, "Permits value must be positive");
        Assert.notNull(period, "Period must not be null");
        Assert.state(period.toNanos() > 0, "Period must be positive");
        this.permits = permits;
        this.period = period;
    }

    /**
     * Creates and returns a new instance of {@link TokenBucketRateLimiter}, configured with
     * the specified permits and period.
     *
     * @return a new instance of {@link TokenBucketRateLimiter}
     */
    @Override
    public RateLimiter createRateLimiter() {
        return new TokenBucketRateLimiter(permits, period);
    }
}
