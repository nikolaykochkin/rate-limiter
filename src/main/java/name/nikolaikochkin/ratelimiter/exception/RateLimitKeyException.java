package name.nikolaikochkin.ratelimiter.exception;

/**
 * The {@code ClientNotFoundException} class represents a custom exception
 * thrown when a client relevant to the application's rate limiting process
 * is not found.
 */
public class RateLimitKeyException extends RuntimeException {
    public RateLimitKeyException() {
    }

    public RateLimitKeyException(String message) {
        super(message);
    }

    public RateLimitKeyException(String message, Throwable cause) {
        super(message, cause);
    }
}
