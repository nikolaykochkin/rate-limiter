package name.nikolaikochkin.ratelimiter;

import name.nikolaikochkin.ratelimiter.service.LimitedService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimiterApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LimitedService limitedService;

    @Test
    void contextLoads() {

    }

    @Test
    void testLimitedController() {
        webTestClient
                .get()
                .uri("api/limit/controller")
                .header("X-FORWARDED-FOR", "1.1.1.1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().isEmpty();
    }

    @Test
    void testLimitedService() {
        StepVerifier.create(limitedService.limitedMonoMethod("123"))
                .verifyComplete();
    }
}
