package name.nikolaikochkin.ratelimiter.key.provider;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.key.model.ClassMethodNameRateLimitKey;
import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import org.aspectj.lang.ProceedingJoinPoint;
import reactor.core.publisher.Mono;

@Slf4j
public class ClassMethodNameKeyProvider implements RateLimitKeyProvider {
    private String className;
    private String methodName;

    @Override
    public Mono<RateLimitKey> getRateLimitKey() {
        log.debug("Providing key for class {} and method {}", className, methodName);
        return Mono.fromCallable(() -> new ClassMethodNameRateLimitKey(className, methodName));
    }

    @Override
    public void init(ProceedingJoinPoint joinPoint) {
        className = joinPoint.getSignature().getDeclaringTypeName();
        methodName = joinPoint.getSignature().getName();
    }
}
