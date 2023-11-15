package name.nikolaikochkin.ratelimiter.service.key.provider;

import lombok.extern.slf4j.Slf4j;
import name.nikolaikochkin.ratelimiter.exception.RateLimitKeyException;
import name.nikolaikochkin.ratelimiter.service.key.model.RateLimitKey;
import name.nikolaikochkin.ratelimiter.service.key.model.RemoteHostAddressRateLimitKey;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

@Slf4j
@Service
public class RemoteHostAddressKeyProvider implements RateLimitKeyProvider {

    @Override
    public Mono<RateLimitKey> getRateLimitKey(ProceedingJoinPoint joinPoint) {
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
