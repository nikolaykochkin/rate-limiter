package name.nikolaikochkin.ratelimiter.service.factory;

import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;

public interface RateLimiterFactory {
    RateLimiter createRateLimiter();
}
