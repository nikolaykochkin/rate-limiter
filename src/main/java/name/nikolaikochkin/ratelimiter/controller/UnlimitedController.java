package name.nikolaikochkin.ratelimiter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * The {@code UnlimitedController} class is a Spring REST controller
 * that provides an endpoint demonstrating a non-rate-limited operation.
 *
 * <p>This controller serves as a contrast to rate-limited operations.</p>
 *
 * <p>Endpoint:</p>
 * <ul>
 *     <li>{@code /api/unlimited} - Demonstrates an unrestricted Mono endpoint at the controller level.</li>
 * </ul>
 */
@Slf4j
@RestController
@RequestMapping("api/unlimited")
public class UnlimitedController {
    @GetMapping
    public Mono<Void> unlimitedControllerMethod() {
        log.debug("Handle unlimited controller Mono method");
        return Mono.empty();
    }
}
