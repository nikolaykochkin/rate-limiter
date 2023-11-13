package name.nikolaikochkin.ratelimiter.key.provider;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.exception.RateLimitKeyException;
import name.nikolaikochkin.ratelimiter.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.key.model.RemoteHostAddressRateLimitKey;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
public class RemoteHostAddressKeyProvider implements RateLimitKeyProvider {

    @Override
    public Mono<RateLimitKey> getRateLimitKey() {
        log.debug("Providing key for remote host address of server http request");
        return Mono.deferContextual(Mono::just)
                .filter(contextView -> contextView.hasKey(ServerHttpRequest.class))
                .map(contextView -> contextView.get(ServerHttpRequest.class))
                .flatMap(this::rateLimitKeyFromHttpRequest);
    }

    public Mono<RemoteHostAddressRateLimitKey> rateLimitKeyFromHttpRequest(ServerHttpRequest request) {
        return Optional.ofNullable(request)
                .map(ServerHttpRequest::getRemoteAddress)
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .map(RemoteHostAddressRateLimitKey::new)
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new RateLimitKeyException("Remote host address not found")));
    }
}
