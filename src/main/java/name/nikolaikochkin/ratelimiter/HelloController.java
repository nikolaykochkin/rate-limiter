package name.nikolaikochkin.ratelimiter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/hello")
public class HelloController {
    @GetMapping
    public Mono<Void> hell() {
        return Mono.empty();
    }
}
