package name.nikolaikochkin.ratelimiter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RateLimiterApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void contextLoads() {

    }

    @Test
    void test1() {
        webTestClient
                .get()
                .uri("api/limit/controller")
                .header("X-FORWARDED-FOR", "1.1.1.1")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody().isEmpty();
    }
}
