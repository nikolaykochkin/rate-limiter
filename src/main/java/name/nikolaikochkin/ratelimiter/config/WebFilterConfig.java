package name.nikolaikochkin.ratelimiter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

/**
 * Configuration class for WebFlux filters.
 */
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFilterConfig {
    /**
     * Method to configure the WebFlux filter to enrich the context with a {@link ServerHttpRequest} object.
     */
    @Bean
    public WebFilter serverHttpRequestWebFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .contextWrite(context -> context.put(ServerHttpRequest.class, exchange.getRequest()));
    }
}
