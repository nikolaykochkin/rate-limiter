package name.nikolaikochkin.ratelimiter.key.provider;

import name.nikolaikochkin.ratelimiter.exception.RateLimitKeyException;
import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import org.aspectj.lang.ProceedingJoinPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;

public interface RateLimitKeyProvider {
    static Flux<RateLimitKeyProvider> createInstances(Class<? extends RateLimitKeyProvider>[] classes, ProceedingJoinPoint joinPoint) {
        return Flux.just(classes)
                .handle((aClass, sink) -> {
                    try {
                        RateLimitKeyProvider rateLimitKeyProvider = aClass.getDeclaredConstructor().newInstance();
                        rateLimitKeyProvider.init(joinPoint);
                        sink.next(rateLimitKeyProvider);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                             NoSuchMethodException e) {
                        sink.error(new RateLimitKeyException("Couldn't create instance of " + aClass.getName(), e));
                    }
                });

    }

    Mono<RateLimitKey> getRateLimitKey();

    default void init(ProceedingJoinPoint joinPoint) {

    }
}
