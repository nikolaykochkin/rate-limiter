package name.nikolaikochkin.ratelimiter.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.service.ClientService;
import name.nikolaikochkin.ratelimiter.exception.RateLimitExceededException;
import name.nikolaikochkin.ratelimiter.service.RateLimitService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final ClientService clientService;
    private final RateLimitService rateLimitService;

    @Pointcut("@annotation(name.nikolaikochkin.ratelimiter.aspect.RateLimit)")
    public void rateLimitMethods() {
    }

    @Around("rateLimitMethods()")
    public Mono<?> applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Apply rate limiting");
        return Mono.deferContextual(Mono::just)
                .map(contextView -> contextView.<ServerHttpRequest>getOrDefault(ServerHttpRequest.class, null))
                .flatMap(clientService::clientFromHttpRequest)
                .map(rateLimitService::allowClientRequest)
                .flatMap(allowed -> allowed ? Mono.empty() : Mono.error(new RateLimitExceededException()))
                .switchIfEmpty((Mono<?>) joinPoint.proceed());
    }
}
