package name.nikolaikochkin.ratelimiter.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.exception.RateLimitExceededException;
import name.nikolaikochkin.ratelimiter.service.ClientService;
import name.nikolaikochkin.ratelimiter.service.RateLimitService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {
    private final ClientService clientService;
    private final RateLimitService rateLimitService;

    @Pointcut("@annotation(name.nikolaikochkin.ratelimiter.aspect.RateLimitAsync) && execution(reactor.core.publisher.Mono *.*(..))")
    public void rateLimitAsyncMethodReturnsMono() {
    }

    @Pointcut("@annotation(name.nikolaikochkin.ratelimiter.aspect.RateLimitAsync) && execution(reactor.core.publisher.Flux *.*(..))")
    public void rateLimitAsyncMethodReturnsFlux() {
    }

    @Around("rateLimitAsyncMethodReturnsMono()")
    public Mono<?> applyAsyncRateLimitingMono(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Apply async rate limiting Mono");
        return checkLimits().switchIfEmpty((Mono<?>) joinPoint.proceed());
    }

    @Around("rateLimitAsyncMethodReturnsFlux()")
    public Flux<?> applyAsyncRateLimitingFlux(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Apply async rate limiting Flux");
        return checkLimits().flux().switchIfEmpty((Flux<?>) joinPoint.proceed());
    }

    private Mono<Object> checkLimits() {
        return Mono.deferContextual(Mono::just)
                .map(contextView -> contextView.<ServerHttpRequest>getOrDefault(ServerHttpRequest.class, null))
                .flatMap(clientService::clientFromHttpRequest)
                .map(rateLimitService::allowClientRequest)
                .flatMap(allowed -> allowed ? Mono.empty() : Mono.error(new RateLimitExceededException()));
    }
}
