package name.nikolaikochkin.ratelimiter.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@code RateLimitAsync} annotation is used to mark methods that should be subjected to
 * asynchronous rate limiting.
 *
 * <p>This annotation can be applied to methods in Spring Boot controllers
 * or services where rate limiting on an asynchronous execution model is required.</p>
 *
 * <p>When applied, it indicates that the method's execution should be rate-limited, considering
 * the application's overall rate limiting strategy and the specifics of the asynchronous processing
 * model.</p>
 *
 * <p>Usage of this annotation requires an aspect or interceptor in the application context that
 * can handle the logic associated with this annotation.</p>
 *
 * @see RateLimitAspect
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimitAsync {
}
