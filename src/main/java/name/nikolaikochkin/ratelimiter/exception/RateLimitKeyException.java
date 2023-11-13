package name.nikolaikochkin.ratelimiter.exception;

/**
 * The {@code RateLimitKeyException} class represents a custom exception
 * thrown when a rate limit key couldn't be provided.
 */
public class RateLimitKeyException extends RuntimeException {
    public RateLimitKeyException(String message) {
        super(message);
    }

    public RateLimitKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
