package name.nikolaikochkin.ratelimiter.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.exception.RateLimitExceededException;
import name.nikolaikochkin.ratelimiter.exception.RateLimitKeyException;
import name.nikolaikochkin.ratelimiter.service.key.RateLimitKeyService;
import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.key.provider.RateLimitKeyProvider;
import name.nikolaikochkin.ratelimiter.service.limiter.RateLimitService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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
    private final RateLimitService rateLimitService;
    private final RateLimitKeyService rateLimitKeyService;

    /**
     * The around advice that implements the rate-limiting logic.
     * It intercepts calls to methods annotated with {@link RateLimitAsync} annotation
     * and applies the rate-limiting checks.
     *
     * @param joinPoint The join point representing the intercepted method call.
     * @return The result of the method call if the rate limit check passes.
     * @throws RateLimitExceededException if the rate limit is exceeded.
     */
    @Around("@annotation(rateLimitAsync) && execution(reactor.core.publisher.Mono *(..))")
    public Mono<?> applyAsyncRateLimitingMono(ProceedingJoinPoint joinPoint, RateLimitAsync rateLimitAsync) throws Throwable {
        log.debug("Apply async rate limiting Mono");
        return checkLimits(joinPoint, rateLimitAsync).switchIfEmpty((Mono<?>) joinPoint.proceed());
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
    @Around("@annotation(rateLimitAsync) && execution(reactor.core.publisher.Flux *(..))")
    public Flux<?> applyAsyncRateLimitingFlux(ProceedingJoinPoint joinPoint, RateLimitAsync rateLimitAsync) throws Throwable {
        log.debug("Apply async rate limiting Flux");
        return checkLimits(joinPoint, rateLimitAsync).flux().switchIfEmpty((Flux<?>) joinPoint.proceed());
    }

    /**
     * The method creates instances of all {@link RateLimitKeyProvider} classes
     * passed through the {@link RateLimitAsync} annotation.
     * Each provider creates the {@link RateLimitKey} key.
     * The first non-null key will be checked by {@link RateLimitService}.
     *
     * @return {@code Mono.empty()} if request allowed
     * {@code Mono.error()} if rate limit key couldn't be provided or the rate limit is exceeded.
     */
    private Mono<Object> checkLimits(ProceedingJoinPoint joinPoint, RateLimitAsync rateLimitAsync) {
        return rateLimitKeyService.getRateLimitKeys(rateLimitAsync.value(), joinPoint)
                .next()
                .switchIfEmpty(Mono.error(() -> new RateLimitKeyException("Key not found")))
                .flatMap(rateLimitKey ->
                        rateLimitService.allowRequest(rateLimitKey)
                                .flatMap(allowed -> allowed
                                        ? Mono.empty()
                                        : Mono.error(() -> new RateLimitExceededException(rateLimitKey + " has exceeded his limit.")))
                );
    }
}
