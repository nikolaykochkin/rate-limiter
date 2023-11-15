package name.nikolaikochkin.ratelimiter.service.key.provider;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.service.key.model.ClassMethodNameRateLimitKey;
import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ClassMethodNameKeyProvider implements RateLimitKeyProvider {
    @Override
    public Mono<RateLimitKey> getRateLimitKey(ProceedingJoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        log.debug("Providing key for class {} and method {}", className, methodName);
        return Mono.fromCallable(() -> new ClassMethodNameRateLimitKey(className, methodName));
    }
}
