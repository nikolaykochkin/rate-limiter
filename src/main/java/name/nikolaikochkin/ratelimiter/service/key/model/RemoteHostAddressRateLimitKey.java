package name.nikolaikochkin.ratelimiter.service.key.model;

public record RemoteHostAddressRateLimitKey(String hostAddress) implements RateLimitKey {
}
