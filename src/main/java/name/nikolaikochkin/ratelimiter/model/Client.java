package name.nikolaikochkin.ratelimiter.model;

/**
 * Represents a client in the rate-limiting context.
 * This class holds information about a client IP address, which may be used in rate-limiting decisions.
 */
public record Client(String ipAddress) {
}
