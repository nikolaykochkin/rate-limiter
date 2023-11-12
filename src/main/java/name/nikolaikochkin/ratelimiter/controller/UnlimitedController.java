package name.nikolaikochkin.ratelimiter.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

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
