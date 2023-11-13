package name.nikolaikochkin.ratelimiter.algorithm;

import java.time.Duration;
import java.util.Objects;

/**
 * The {@code TokenBucketRateLimiter} class implements rate limiting using the token bucket algorithm.
 *
 * <p>In this implementation, tokens are added to the bucket at a fixed rate. Each request consumes a token
 * from the bucket, and if no tokens are available, the request is either rejected or queued, depending on
 * the implementation details.</p>
 *
 * <p>The rate limiter is configured with a maximum number of tokens (the capacity of the bucket) and the
 * rate at which tokens are replenished. The implementation ensures thread-safety and is suitable for
 * high-throughput environments.</p>
 *
 * @see RateLimiter
 */
public class TokenBucketRateLimiter implements RateLimiter {
    private final long capacity;
    private long availableTokens;
    private final long nanosToGenerationToken;
    private long lastRefillNanotime;

    /**
     * Constructs a new {@code TokenBucketRateLimiter} with the specified capacity and refill rate.
     *
     * @param permits The maximum number of tokens that the bucket can hold.
     * @param period  The rate at which tokens are replenished in the bucket, typically in tokens per second.
     * @throws IllegalArgumentException if either permits or period is negative or zero.
     */
    public TokenBucketRateLimiter(long permits, Duration period) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits value should be positive");
        }
        if (Objects.isNull(period) || period.isZero() || period.isNegative()) {
            throw new IllegalArgumentException("Period value should be positive");
        }
        this.nanosToGenerationToken = period.toNanos() / permits;
        this.lastRefillNanotime = System.nanoTime();
        this.capacity = permits;
        this.availableTokens = permits;
    }

    /**
     * Attempts to consume one token from the bucket.
     *
     * @return {@code true} if the requested number of tokens were successfully consumed;
     * {@code false} if insufficient tokens are available.
     */
    @Override
    public boolean tryConsume() {
        return tryConsume(1);
    }

    /**
     * Attempts to consume a specified number of tokens from the bucket.
     *
     * @param permits The number of tokens to consume from the bucket.
     * @return {@code true} if the requested number of tokens were successfully consumed;
     * {@code false} if insufficient tokens are available.
     * @throws IllegalArgumentException if the number of requested permits is negative or zero.
     */
    @Override
    synchronized public boolean tryConsume(int permits) {
        if (permits <= 0) {
            throw new IllegalArgumentException("Permits value should be positive");
        }
        refill();
        if (availableTokens < permits) {
            return false;
        } else {
            availableTokens -= permits;
            return true;
        }
    }

    private void refill() {
        long now = System.nanoTime();
        long nanosSinceLastRefill = now - lastRefillNanotime;
        if (nanosSinceLastRefill <= nanosToGenerationToken) {
            return;
        }
        long tokensSinceLastRefill = nanosSinceLastRefill / nanosToGenerationToken;
        availableTokens = Math.min(capacity, availableTokens + tokensSinceLastRefill);
        lastRefillNanotime += tokensSinceLastRefill * nanosToGenerationToken;
    }

    @Override
    public String toString() {
        return "TokenBucketRateLimiter{" +
                "capacity=" + capacity +
                ", availableTokens=" + availableTokens +
                ", nanosToGenerationToken=" + nanosToGenerationToken +
                ", lastRefillNanotime=" + lastRefillNanotime +
                '}';
    }
}
