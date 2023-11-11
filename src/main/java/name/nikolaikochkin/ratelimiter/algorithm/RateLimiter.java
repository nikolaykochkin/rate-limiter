package name.nikolaikochkin.ratelimiter.algorithm;

public interface RateLimiter {
    boolean tryConsume(int permits);
}
