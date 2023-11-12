package name.nikolaikochkin.ratelimiter.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.exception.RateLimitExceededException;
import name.nikolaikochkin.ratelimiter.service.ClientService;
import name.nikolaikochkin.ratelimiter.service.limiter.RateLimitService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The {@code RateLimitAspect} class implements an aspect for rate limiting in a Spring Boot application.
 *
 * <p>This aspect is designed to intercept method calls annotated with {@link RateLimitAsync} annotations and enforce
 * rate-limiting logic as defined in the application's configuration.</p>
 *
 * <p>This aspect should be used in conjunction with appropriate rate limiting services and configurations
 * to effectively manage application traffic.</p>
 *
 * @see RateLimitAsync
 */
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

    /**
     * The around advice that implements the rate-limiting logic.
     * It intercepts calls to methods annotated with {@link RateLimitAsync} annotation
     * and applies the rate-limiting checks.
     *
     * @param joinPoint The join point representing the intercepted method call.
     * @return The result of the method call if the rate limit check passes.
     * @throws RateLimitExceededException if the rate limit is exceeded.
     */
    @Around("rateLimitAsyncMethodReturnsMono()")
    public Mono<?> applyAsyncRateLimitingMono(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Apply async rate limiting Mono");
        return checkLimits().switchIfEmpty((Mono<?>) joinPoint.proceed());
    }

    /**
     * The around advice that implements the rate-limiting logic.
     * It intercepts calls to methods annotated with {@link RateLimitAsync} annotations
     * and applies the rate-limiting checks.
     *
     * @param joinPoint The join point representing the intercepted method call.
     * @return The result of the method call if the rate limit check passes.
     * @throws RateLimitExceededException if the rate limit is exceeded.
     */
    @Around("rateLimitAsyncMethodReturnsFlux()")
    public Flux<?> applyAsyncRateLimitingFlux(ProceedingJoinPoint joinPoint) throws Throwable {
        log.debug("Apply async rate limiting Flux");
        return checkLimits().flux().switchIfEmpty((Flux<?>) joinPoint.proceed());
    }

    /**
     * The method receives a request from the context,
     * transforms the request into a client object, and calls the rate-limiting logic.
     *
     * @return {@code Mono.empty()} if client request allowed
     * {@code Mono.error()} if current context does not contain request or the rate limit is exceeded.
     */
    private Mono<Object> checkLimits() {
        return Mono.deferContextual(Mono::just)
                .mapNotNull(contextView -> contextView.<ServerHttpRequest>getOrDefault(ServerHttpRequest.class, null))
                .flatMap(clientService::clientFromHttpRequest)
                .flatMap(client -> {
                    if (rateLimitService.allowClientRequest(client)) {
                        return Mono.empty();
                    } else {
                        return Mono.error(() -> new RateLimitExceededException(client + " has exceeded his limit."));
                    }
                });
    }
}
