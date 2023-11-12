package name.nikolaikochkin.ratelimiter.service.limiter;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.model.Client;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
public class InMemoryRateLimitService implements RateLimitService {

    private final RateLimiterFactory rateLimiterFactory;
    private final ConcurrentMap<Client, RateLimiter> buckets;

    public InMemoryRateLimitService(RateLimiterFactory rateLimiterFactory) {
        this.rateLimiterFactory = rateLimiterFactory;
        this.buckets = new ConcurrentHashMap<>();
    }

    @Override
    public boolean allowClientRequest(Client client) {
        RateLimiter rateLimiter = buckets.computeIfAbsent(client, key -> rateLimiterFactory.createRateLimiter());
        log.debug("Client: {}, has limits: {}", client, rateLimiter);
        return rateLimiter.tryConsume(1);
    }
}
