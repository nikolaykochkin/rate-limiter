package name.nikolaikochkin.ratelimiter.service.limiter;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.algorithm.RateLimiter;
import name.nikolaikochkin.ratelimiter.model.Client;
import name.nikolaikochkin.ratelimiter.service.factory.RateLimiterFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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
    private final ConcurrentMap<Client, RateLimiter> buckets;

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
    public boolean allowClientRequest(Client client) {
        Assert.notNull(client, "Client must not be null");
        RateLimiter rateLimiter = buckets.computeIfAbsent(client, key -> rateLimiterFactory.createRateLimiter());
        log.debug("Client: {}, has limits: {}", client, rateLimiter);
        return rateLimiter.tryConsume(1);
    }
}
