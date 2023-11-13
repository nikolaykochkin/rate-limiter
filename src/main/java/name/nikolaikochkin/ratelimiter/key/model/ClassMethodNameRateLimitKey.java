package name.nikolaikochkin.ratelimiter.key.model;

public record ClassMethodNameRateLimitKey(String className, String methodName) implements RateLimitKey {
}
