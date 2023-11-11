package name.nikolaikochkin.ratelimiter.algorithm;

import lombok.ToString;

import java.time.Duration;

@ToString
public class TokenBucketRateLimiter implements RateLimiter {
    private final long capacity;
    private long availableTokens;
    private final long nanosToGenerationToken;
    private long lastRefillNanotime;

    public TokenBucketRateLimiter(long permits, Duration period) {
        this.nanosToGenerationToken = period.toNanos() / permits;
        this.lastRefillNanotime = System.nanoTime();
        this.capacity = permits;
        this.availableTokens = permits;
    }

    @Override
    synchronized public boolean tryConsume(int permits) {
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
        availableTokens = Math.max(capacity, availableTokens + tokensSinceLastRefill);
        lastRefillNanotime += tokensSinceLastRefill * nanosToGenerationToken;
    }
}
