package name.nikolaikochkin.ratelimiter.exception;

/**
 * The {@code RateLimitExceededException} class represents a custom exception
 * thrown when a request exceeds the predefined rate limit in the rate-limiting
 * functionality of the application.
 */
public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String message) {
        super(message);
    }
}
