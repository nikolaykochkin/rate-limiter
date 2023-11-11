package name.nikolaikochkin.ratelimiter.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.WebFilter;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFilterConfig {
    @Bean
    public WebFilter serverHttpRequestWebFilter() {
        return (exchange, chain) -> chain.filter(exchange)
                .contextWrite(context -> context.put(ServerHttpRequest.class, exchange.getRequest()));
    }
}
