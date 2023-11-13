package name.nikolaikochkin.ratelimiter.key.model;

public record RemoteHostAddressRateLimitKey(String hostAddress) implements RateLimitKey {
}
