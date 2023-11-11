package name.nikolaikochkin.ratelimiter.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import name.nikolaikochkin.ratelimiter.aspect.RateLimitAsync;
import name.nikolaikochkin.ratelimiter.service.LimitedService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("api/limit")
@RequiredArgsConstructor
public class LimitedController {
    private final LimitedService limitedService;

    @GetMapping("controller")
    @RateLimitAsync
    public Mono<Void> limitedControllerMonoMethod() {
        log.debug("Handle limited controller Mono method");
        return Mono.empty();
    }

    @GetMapping("controller/flux")
    @RateLimitAsync
    public Flux<Void> limitedControllerFluxMethod() {
        log.debug("Handle limited controller Flux method");
        return Flux.empty();
    }

    @GetMapping("service")
    public Mono<Void> limitedServiceMonoMethod() {
        log.debug("Handle limited service Mono method");
        return limitedService.limitedMonoMethod("test");
    }

    @GetMapping("service/flux")
    public Flux<Void> limitedServiceFluxMethod() {
        log.debug("Handle limited service Flux method");
        return limitedService.limitedFluxMethod();
    }
}
