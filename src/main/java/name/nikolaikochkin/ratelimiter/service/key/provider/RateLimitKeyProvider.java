package name.nikolaikochkin.ratelimiter.service.key.provider;

import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import org.aspectj.lang.ProceedingJoinPoint;
import reactor.core.publisher.Mono;

public interface RateLimitKeyProvider {
    Mono<RateLimitKey> getRateLimitKey(ProceedingJoinPoint joinPoint);
}
