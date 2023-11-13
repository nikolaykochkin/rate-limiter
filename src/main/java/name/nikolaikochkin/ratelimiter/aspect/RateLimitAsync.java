package name.nikolaikochkin.ratelimiter.aspect;

import name.nikolaikochkin.ratelimiter.key.provider.RateLimitKeyProvider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code RateLimitAsync} annotation is used to mark methods that should be subjected to
 * asynchronous rate limiting.
 *
 * <p>Annotated methods should return {@code Mono} or {@code Flux}</p>
 *
 * @see RateLimitAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimitAsync {
    /**
     * Array of key providers
     */
    Class<? extends RateLimitKeyProvider>[] value();
}
