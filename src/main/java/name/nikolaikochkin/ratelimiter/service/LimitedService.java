package name.nikolaikochkin.ratelimiter.service;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.aspect.RateLimitAsync;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class LimitedService {
    @RateLimitAsync
    public Mono<Void> limitedMonoMethod(String test) {
        log.debug("Limited service Mono method. Argument: {}", test);
        return Mono.empty();
    }

    @RateLimitAsync
    public Flux<Void> limitedFluxMethod() {
        log.debug("Limited service Flux method");
        return Flux.empty();
    }
}
