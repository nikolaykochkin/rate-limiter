package name.nikolaikochkin.ratelimiter.service.key.model;

public record ClassMethodNameRateLimitKey(String className, String methodName) implements RateLimitKey {
}
