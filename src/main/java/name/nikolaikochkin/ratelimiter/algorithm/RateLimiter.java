package name.nikolaikochkin.ratelimiter.algorithm;

/**
 * The {@code RateLimiter} interface defines the methods for implementing rate-limiting logic.
 * <p>Implementations of this interface should ensure that the rate of requests does not exceed a specified limit.</p>
 * <p>Implementations of this interface are expected to be thread-safe and performant under high-load conditions.</p>
 */
public interface RateLimiter {

    /**
     * Attempts to consume a specified number of permits from the rate limiter.
     *
     * @param permits the number of permits to consume.
     * @return {@code true} if the requested number of permits were successfully consumed, {@code false} otherwise.
     * @throws IllegalArgumentException if the number of permits is negative.
     */
    boolean tryConsume(int permits);
}
