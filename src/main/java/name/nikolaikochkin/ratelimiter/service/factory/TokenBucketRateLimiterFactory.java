package name.nikolaikochkin.ratelimiter.service.factory;

import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.algorithm.TokenBucketRateLimiter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;

@Service
public class TokenBucketRateLimiterFactory implements RateLimiterFactory {
    private final long permits;
    private final Duration period;

    public TokenBucketRateLimiterFactory(@Value("${application.ratelimit.permits}") long permits,
                                         @Value("${application.ratelimit.period}") Duration period) {
        Assert.state(permits > 0, "Permits value must be positive");
        Assert.notNull(period, "Period must not be null");
        Assert.state(period.toNanos() > 0, "Period must be positive");
        this.permits = permits;
        this.period = period;
    }

    @Override
    public RateLimiter createRateLimiter() {
        return new TokenBucketRateLimiter(permits, period);
    }
}
