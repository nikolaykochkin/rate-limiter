package name.nikolaikochkin.ratelimiter.service;

import name.nikolaikochkin.ratelimiter.exception.ClientNotFoundException;
import name.nikolaikochkin.ratelimiter.model.Client;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Optional;

@Service
public class ClientService {
    public Mono<Client> clientFromHttpRequest(ServerHttpRequest request) {
        return Optional.ofNullable(request)
                .map(ServerHttpRequest::getRemoteAddress)
                .map(InetSocketAddress::getAddress)
                .map(InetAddress::getHostAddress)
                .map(Client::new)
                .map(Mono::just)
                .orElseGet(() -> Mono.error(new ClientNotFoundException()));
    }
}
