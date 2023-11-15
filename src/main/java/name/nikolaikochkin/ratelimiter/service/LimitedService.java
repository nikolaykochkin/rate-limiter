package name.nikolaikochkin.ratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.aspect.RateLimitAsync;
import name.nikolaikochkin.ratelimiter.service.key.provider.ClassMethodNameKeyProvider;
import name.nikolaikochkin.ratelimiter.service.key.provider.RemoteHostAddressKeyProvider;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * The {@code LimitedService} class is a Spring service that provides methods
 * demonstrating rate-limited operations using reactive types Mono and Flux.
 * This class showcases how rate limiting can be integrated at the service level
 * within a reactive application.
 *
 * <p>Both methods in this service are annotated with {@code @RateLimitAsync}, indicating
 * that they are subject to asynchronous rate limiting. This ensures that requests
 * exceeding the defined rate limits are handled appropriately, either by queuing, rejecting,
 * or other custom behaviors defined in the rate limiting logic.</p>
 *
 * @see RateLimitAsync
 */
@Slf4j
@Service
public class LimitedService {
    @RateLimitAsync({
            RemoteHostAddressKeyProvider.class,
            ClassMethodNameKeyProvider.class
    })
    public Mono<Void> limitedMonoMethod(String test) {
        log.debug("Limited service Mono method. Argument: {}", test);
        return Mono.empty();
    }

    @RateLimitAsync({
            RemoteHostAddressKeyProvider.class,
            ClassMethodNameKeyProvider.class
    })
    public Flux<Void> limitedFluxMethod() {
        log.debug("Limited service Flux method");
        return Flux.empty();
    }
}
