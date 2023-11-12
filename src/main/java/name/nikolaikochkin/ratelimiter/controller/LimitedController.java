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

/**
 * The {@code LimitedController} class is a Spring REST controller that provides endpoints
 * for demonstrating rate-limited operations using both Mono and Flux reactive types from
 * Project Reactor.
 * <p>
 * This controller showcases the application of rate limiting on both controller and service level methods.
 * The controller includes methods that return Mono<Void> and Flux<Void>
 * to demonstrate how rate limiting can be applied to reactive streams in a non-blocking manner.
 * The {@link RateLimitAsync} annotation is used to enforce rate limiting asynchronously.
 *
 * <p>Endpoints:</p>
 * <ul>
 *     <li>{@code /api/limit/controller} - Demonstrates a rate-limited Mono endpoint at the controller level.</li>
 *     <li>{@code /api/limit/controller/flux} - Demonstrates a rate-limited Flux endpoint at the controller level.</li>
 *     <li>{@code /api/limit/service} - Calls a rate-limited Mono method from the {@link LimitedService}.</li>
 *     <li>{@code /api/limit/service/flux} - Calls a rate-limited Flux method from the {@link LimitedService}.</li>
 * </ul>
 *
 * @see LimitedService
 */
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
