package name.nikolaikochkin.ratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.algorithm.TokenBucketRateLimiter;
import name.nikolaikochkin.ratelimiter.model.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class InMemoryRateLimitService implements RateLimitService {

    private final long permits;
    private final Duration period;
    private final ConcurrentMap<Client, RateLimiter> buckets;

    public InMemoryRateLimitService(@Value("${application.ratelimit.permits}") long permits,
                                    @Value("${application.ratelimit.period}") Duration period) {
        this.permits = permits;
        this.period = period;
        buckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowClientRequest(Client client) {
        RateLimiter rateLimiter = buckets.computeIfAbsent(client, key -> new TokenBucketRateLimiter(permits, period));
        log.debug("Client: {}, has limits: {}", client, rateLimiter);
        return rateLimiter.tryConsume(1);
    }
}
